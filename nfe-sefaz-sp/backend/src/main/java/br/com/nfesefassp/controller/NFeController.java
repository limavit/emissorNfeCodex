package br.com.nfesefassp.controller;

import br.com.nfesefassp.model.*;
import br.com.nfesefassp.service.*;

import br.com.nfesefassp.util.AccessKeyService;
import br.com.nfesefassp.service.NFeEventService;
import br.com.nfesefassp.service.NFeInutilizationService;
import br.com.nfesefassp.service.NFeStatusService;
import br.com.nfesefassp.service.NFeTransmissionService;
import br.com.nfesefassp.service.StorageService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies/{companyId}/nfe")
public class NFeController {
    private final JdbcTemplate jdbc;
    private final AccessKeyService accessKeyService;
    private final NFeXmlBuilderService xmlBuilder;
    private final NFeXmlValidationService xmlValidation;
    private final NFeSignatureService signatureService;
    private final NFeTransmissionService transmissionService;
    private final NFeStatusService statusService;
    private final NFeEventService eventService;
    private final NFeInutilizationService inutilizationService;
    private final DanfeService danfeService;
    private final StorageService storageService;

    public NFeController(JdbcTemplate jdbc, AccessKeyService accessKeyService, NFeXmlBuilderService xmlBuilder,
                         NFeXmlValidationService xmlValidation, NFeSignatureService signatureService,
                         NFeTransmissionService transmissionService, NFeStatusService statusService,
                         NFeEventService eventService, NFeInutilizationService inutilizationService,
                         DanfeService danfeService, StorageService storageService) {
        this.jdbc = jdbc;
        this.accessKeyService = accessKeyService;
        this.xmlBuilder = xmlBuilder;
        this.xmlValidation = xmlValidation;
        this.signatureService = signatureService;
        this.transmissionService = transmissionService;
        this.statusService = statusService;
        this.eventService = eventService;
        this.inutilizationService = inutilizationService;
        this.danfeService = danfeService;
        this.storageService = storageService;
    }

    @GetMapping
    public List<Map<String, Object>> list(@PathVariable UUID companyId) {
        return jdbc.queryForList("select * from nfe where company_id = ? order by created_at desc", companyId);
    }

    @PostMapping
    public Map<String, Object> create(@PathVariable UUID companyId, @RequestBody Map<String, Object> body) {
        Map<String, Object> company = jdbc.queryForMap("select * from companies where id = ?", companyId);
        UUID id = UUID.randomUUID();
        int series = ((Number) company.get("default_series")).intValue();
        long number = ((Number) company.get("next_nfe_number")).longValue();
        String key = accessKeyService.generate("35", OffsetDateTime.now(), String.valueOf(company.get("cnpj")), "55", series, number, "1", 10000000 + (int) (number % 89999999));
        jdbc.update("""
                insert into nfe (id, company_id, customer_id, series, number, access_key, nature_operation, operation_type,
                destination_type, issue_datetime, purpose, presence_indicator, emission_type)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, now(), ?, ?, ?)
                """, id, companyId, body.get("customerId"), series, number, key,
                body.getOrDefault("natureOperation", company.get("default_nature_operation")),
                body.getOrDefault("operationType", "SAIDA"), body.getOrDefault("destinationType", "INTERNA"),
                body.getOrDefault("purpose", "NORMAL"), body.getOrDefault("presenceIndicator", "9"),
                body.getOrDefault("emissionType", "1"));
        jdbc.update("update companies set next_nfe_number = next_nfe_number + 1 where id = ?", companyId);
        return get(companyId, id);
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable UUID companyId, @PathVariable UUID id) {
        return jdbc.queryForMap("select * from nfe where company_id = ? and id = ?", companyId, id);
    }

    @PostMapping("/{id}/validate")
    public Map<String, Object> validate(@PathVariable UUID companyId, @PathVariable UUID id) {
        Map<String, Object> nfe = get(companyId, id);
        if (!"RASCUNHO".equals(nfe.get("status")) && !"REJEITADA".equals(nfe.get("status"))) {
            throw new IllegalStateException("NF-e nao esta em status editavel.");
        }
        jdbc.update("update nfe set status = 'VALIDADA', updated_at = now() where id = ?", id);
        return Map.of("status", "VALIDADA");
    }

    @PostMapping("/{id}/generate-xml")
    public Map<String, Object> generateXml(@PathVariable UUID companyId, @PathVariable UUID id) {
        Map<String, Object> nfe = get(companyId, id);
        String xml = xmlBuilder.build(nfe);
        xmlValidation.validate(xml);
        String path = storageService.saveText("nfe/homologacao/" + companyId + "/" + nfe.get("access_key") + "/unsigned.xml", xml);
        jdbc.update("insert into nfe_xml_documents (nfe_id, document_type, file_path, xml_content) values (?, 'XML_UNSIGNED', ?, ?)", id, path, xml);
        jdbc.update("update nfe set status = 'XML_GERADO', updated_at = now() where id = ?", id);
        return Map.of("status", "XML_GERADO", "path", path);
    }

    @PostMapping("/{id}/sign")
    public Map<String, Object> sign(@PathVariable UUID companyId, @PathVariable UUID id) {
        String xml = jdbc.queryForObject("select xml_content from nfe_xml_documents where nfe_id = ? and document_type = 'XML_UNSIGNED' order by created_at desc limit 1", String.class, id);
        String signed = signatureService.sign(xml);
        String path = storageService.saveText("nfe/homologacao/" + companyId + "/" + id + "/signed.xml", signed);
        jdbc.update("insert into nfe_xml_documents (nfe_id, document_type, file_path, xml_content) values (?, 'XML_SIGNED', ?, ?)", id, path, signed);
        jdbc.update("update nfe set status = 'ASSINADA', updated_at = now() where id = ?", id);
        return Map.of("status", "ASSINADA", "path", path);
    }

    @PostMapping("/{id}/transmit")
    public Map<String, Object> transmit(@PathVariable UUID companyId, @PathVariable UUID id) {
        String xml = jdbc.queryForObject("select xml_content from nfe_xml_documents where nfe_id = ? and document_type = 'XML_SIGNED' order by created_at desc limit 1", String.class, id);
        Map<String, Object> result = transmissionService.transmit(xml);
        jdbc.update("update nfe set status = 'REJEITADA', sefaz_status_code = ?, sefaz_status_reason = ?, updated_at = now() where id = ?",
                result.get("code"), result.get("reason"), id);
        return result;
    }

    @PostMapping("/{id}/consult")
    public Map<String, Object> consult(@PathVariable UUID companyId, @PathVariable UUID id) {
        return statusService.status("SP", "HOMOLOGACAO");
    }

    @PostMapping("/{id}/cancel")
    public Map<String, Object> cancel(@PathVariable UUID companyId, @PathVariable UUID id, @RequestBody Map<String, Object> body) {
        return eventService.cancel(id, String.valueOf(body.get("justification")));
    }

    @PostMapping("/{id}/cce")
    public Map<String, Object> cce(@PathVariable UUID companyId, @PathVariable UUID id, @RequestBody Map<String, Object> body) {
        return eventService.cce(id, String.valueOf(body.get("text")));
    }

    @GetMapping(value = "/{id}/danfe", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] danfe(@PathVariable UUID companyId, @PathVariable UUID id) {
        return danfeService.generate(get(companyId, id));
    }

    @GetMapping("/{id}/xml")
    public String xml(@PathVariable UUID companyId, @PathVariable UUID id) {
        return jdbc.queryForObject("select xml_content from nfe_xml_documents where nfe_id = ? order by created_at desc limit 1", String.class, id);
    }

    @PostMapping("/inutilization")
    public Map<String, Object> inutilization(@PathVariable UUID companyId, @RequestBody Map<String, Object> body) {
        return inutilizationService.inutilize(companyId, body);
    }

    @GetMapping("/inutilization")
    public List<Map<String, Object>> inutilizations(@PathVariable UUID companyId) {
        return jdbc.queryForList("select * from nfe_events where company_id = ? and event_type = 'INUTILIZACAO'", companyId);
    }
}
