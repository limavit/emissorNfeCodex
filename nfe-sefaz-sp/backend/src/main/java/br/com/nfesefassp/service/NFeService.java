package br.com.nfesefassp.service;

import br.com.nfesefassp.model.Company;
import br.com.nfesefassp.model.NFe;
import br.com.nfesefassp.model.NFeDetailResponse;
import br.com.nfesefassp.model.NFeItem;
import br.com.nfesefassp.model.NFeRequest;
import br.com.nfesefassp.model.NFeStatus;
import br.com.nfesefassp.repository.CompanyRepository;
import br.com.nfesefassp.repository.NFeItemRepository;
import br.com.nfesefassp.repository.NFeRepository;
import br.com.nfesefassp.util.AccessKeyService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NFeService {
    private final NFeRepository nfes;
    private final NFeItemRepository nfeItems;
    private final CompanyRepository companies;
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
    private final NFeCalculationService calculationService;

    public NFeService(NFeRepository nfes, NFeItemRepository nfeItems, CompanyRepository companies, JdbcTemplate jdbc,
                      AccessKeyService accessKeyService, NFeXmlBuilderService xmlBuilder,
                      NFeXmlValidationService xmlValidation, NFeSignatureService signatureService,
                      NFeTransmissionService transmissionService, NFeStatusService statusService,
                      NFeEventService eventService, NFeInutilizationService inutilizationService,
                      DanfeService danfeService, StorageService storageService, NFeCalculationService calculationService) {
        this.nfes = nfes;
        this.nfeItems = nfeItems;
        this.companies = companies;
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
        this.calculationService = calculationService;
    }

    public List<NFe> list(UUID companyId) {
        return nfes.findByCompanyIdOrderByCreatedAtDesc(companyId);
    }

    public NFe get(UUID companyId, UUID id) {
        return nfes.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new IllegalArgumentException("NF-e nao encontrada."));
    }

    public NFeDetailResponse detail(UUID companyId, UUID id) {
        NFe nfe = get(companyId, id);
        return new NFeDetailResponse(nfe, nfeItems.findByNfeIdOrderByItemNumber(id));
    }

    @Transactional
    public NFe create(UUID companyId, NFeRequest request) {
        Company company = companies.findById(companyId).orElseThrow(() -> new IllegalArgumentException("Empresa nao encontrada."));
        int series = company.getDefaultSeries();
        long number = company.getNextNfeNumber();
        String key = accessKeyService.generate("35", OffsetDateTime.now(), company.getCnpj(), "55", series, number, "1",
                10000000 + (int) (number % 89999999));
        NFe nfe = nfes.save(NFe.draft(companyId, request, company, key));
        replaceItems(nfe, request);
        company.incrementNextNfeNumber();
        return nfe;
    }

    @Transactional
    public Map<String, Object> validate(UUID companyId, UUID id) {
        NFe nfe = get(companyId, id);
        if (!NFeStatus.RASCUNHO.name().equals(nfe.getStatus()) && !NFeStatus.REJEITADA.name().equals(nfe.getStatus())) {
            throw new IllegalStateException("NF-e nao esta em status editavel.");
        }
        if (nfeItems.countByNfeId(id) == 0) {
            throw new IllegalStateException("Inclua ao menos um item antes de validar a NF-e.");
        }
        nfe.markStatus(NFeStatus.VALIDADA);
        return Map.of("status", NFeStatus.VALIDADA.name());
    }

    @Transactional
    public Map<String, Object> generateXml(UUID companyId, UUID id) {
        NFe nfe = get(companyId, id);
        String xml = xmlBuilder.build(nfe);
        xmlValidation.validate(xml);
        String path = storageService.saveText("nfe/homologacao/" + companyId + "/" + nfe.getAccessKey() + "/unsigned.xml", xml);
        jdbc.update("insert into nfe_xml_documents (nfe_id, document_type, file_path, xml_content) values (?, 'XML_UNSIGNED', ?, ?)", id, path, xml);
        nfe.markStatus(NFeStatus.XML_GERADO);
        return Map.of("status", NFeStatus.XML_GERADO.name(), "path", path);
    }

    @Transactional
    public Map<String, Object> sign(UUID companyId, UUID id) {
        get(companyId, id);
        String xml = jdbc.queryForObject("select xml_content from nfe_xml_documents where nfe_id = ? and document_type = 'XML_UNSIGNED' order by created_at desc limit 1", String.class, id);
        String signed = signatureService.sign(xml);
        String path = storageService.saveText("nfe/homologacao/" + companyId + "/" + id + "/signed.xml", signed);
        jdbc.update("insert into nfe_xml_documents (nfe_id, document_type, file_path, xml_content) values (?, 'XML_SIGNED', ?, ?)", id, path, signed);
        get(companyId, id).markStatus(NFeStatus.ASSINADA);
        return Map.of("status", NFeStatus.ASSINADA.name(), "path", path);
    }

    @Transactional
    public Map<String, Object> transmit(UUID companyId, UUID id) {
        NFe nfe = get(companyId, id);
        String xml = jdbc.queryForObject("select xml_content from nfe_xml_documents where nfe_id = ? and document_type = 'XML_SIGNED' order by created_at desc limit 1", String.class, id);
        Map<String, Object> result = transmissionService.transmit(xml);
        nfe.markSefazResult(NFeStatus.REJEITADA, result.get("code"), result.get("reason"));
        return result;
    }

    public Map<String, Object> consult(UUID companyId) {
        return statusService.status("SP", "HOMOLOGACAO");
    }

    public Map<String, Object> cancel(UUID companyId, UUID id, Map<String, Object> body) {
        get(companyId, id);
        return eventService.cancel(id, String.valueOf(body.get("justification")));
    }

    public Map<String, Object> cce(UUID companyId, UUID id, Map<String, Object> body) {
        get(companyId, id);
        return eventService.cce(id, String.valueOf(body.get("text")));
    }

    public byte[] danfe(UUID companyId, UUID id) {
        return danfeService.generate(get(companyId, id));
    }

    public String xml(UUID companyId, UUID id) {
        get(companyId, id);
        return jdbc.queryForObject("select xml_content from nfe_xml_documents where nfe_id = ? order by created_at desc limit 1", String.class, id);
    }

    public Map<String, Object> inutilization(UUID companyId, Map<String, Object> body) {
        return inutilizationService.inutilize(companyId, body);
    }

    public List<Map<String, Object>> inutilizations(UUID companyId) {
        return jdbc.queryForList("select * from nfe_events where company_id = ? and event_type = 'INUTILIZACAO'", companyId);
    }

    private void replaceItems(NFe nfe, NFeRequest request) {
        nfeItems.deleteByNfeId(nfe.getId());
        if (request.items() == null || request.items().isEmpty()) {
            nfe.applyTotals(calculationService.calculate(List.of()));
            return;
        }
        List<NFeItem> items = new java.util.ArrayList<>();
        int itemNumber = 1;
        for (var itemRequest : request.items()) {
            items.add(NFeItem.from(nfe.getId(), itemNumber++, itemRequest));
        }
        nfeItems.saveAll(items);
        nfe.applyTotals(calculationService.calculate(items.stream().map(NFeItem::toCalculationItem).toList()));
    }
}
