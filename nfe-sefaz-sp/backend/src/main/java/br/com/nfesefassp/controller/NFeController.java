package br.com.nfesefassp.controller;

import br.com.nfesefassp.model.NFe;
import br.com.nfesefassp.model.NFeDetailResponse;
import br.com.nfesefassp.model.NFeRequest;
import br.com.nfesefassp.service.NFeService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies/{companyId}/nfe")
public class NFeController {
    private final NFeService service;

    public NFeController(NFeService service) {
        this.service = service;
    }

    @GetMapping
    public List<NFe> list(@PathVariable UUID companyId) {
        return service.list(companyId);
    }

    @PostMapping
    public NFe create(@PathVariable UUID companyId, @RequestBody NFeRequest request) {
        return service.create(companyId, request);
    }

    @GetMapping("/{id}")
    public NFeDetailResponse get(@PathVariable UUID companyId, @PathVariable UUID id) {
        return service.detail(companyId, id);
    }

    @PostMapping("/{id}/validate")
    public Map<String, Object> validate(@PathVariable UUID companyId, @PathVariable UUID id) {
        return service.validate(companyId, id);
    }

    @PostMapping("/{id}/generate-xml")
    public Map<String, Object> generateXml(@PathVariable UUID companyId, @PathVariable UUID id) {
        return service.generateXml(companyId, id);
    }

    @PostMapping("/{id}/sign")
    public Map<String, Object> sign(@PathVariable UUID companyId, @PathVariable UUID id) {
        return service.sign(companyId, id);
    }

    @PostMapping("/{id}/transmit")
    public Map<String, Object> transmit(@PathVariable UUID companyId, @PathVariable UUID id) {
        return service.transmit(companyId, id);
    }

    @PostMapping("/{id}/consult")
    public Map<String, Object> consult(@PathVariable UUID companyId, @PathVariable UUID id) {
        return service.consult(companyId);
    }

    @PostMapping("/{id}/cancel")
    public Map<String, Object> cancel(@PathVariable UUID companyId, @PathVariable UUID id, @RequestBody Map<String, Object> body) {
        return service.cancel(companyId, id, body);
    }

    @PostMapping("/{id}/cce")
    public Map<String, Object> cce(@PathVariable UUID companyId, @PathVariable UUID id, @RequestBody Map<String, Object> body) {
        return service.cce(companyId, id, body);
    }

    @GetMapping(value = "/{id}/danfe", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] danfe(@PathVariable UUID companyId, @PathVariable UUID id) {
        return service.danfe(companyId, id);
    }

    @GetMapping("/{id}/xml")
    public String xml(@PathVariable UUID companyId, @PathVariable UUID id) {
        return service.xml(companyId, id);
    }

    @PostMapping("/inutilization")
    public Map<String, Object> inutilization(@PathVariable UUID companyId, @RequestBody Map<String, Object> body) {
        return service.inutilization(companyId, body);
    }

    @GetMapping("/inutilization")
    public List<Map<String, Object>> inutilizations(@PathVariable UUID companyId) {
        return service.inutilizations(companyId);
    }
}
