package br.com.nfesefassp.controller;

import br.com.nfesefassp.model.*;
import br.com.nfesefassp.service.*;

import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/companies/{companyId}/certificate")
public class CertificateController {
    private final CertificateService service;

    public CertificateController(CertificateService service) {
        this.service = service;
    }

    @PostMapping
    public Map<String, Object> upload(@PathVariable UUID companyId, @RequestParam MultipartFile file, @RequestParam String password) {
        return service.upload(companyId, file, password);
    }

    @GetMapping
    public Map<String, Object> status(@PathVariable UUID companyId) {
        return service.status(companyId);
    }

    @DeleteMapping
    public void remove(@PathVariable UUID companyId) {
        service.remove(companyId);
    }

    @PostMapping("/validate")
    public Map<String, Object> validate(@PathVariable UUID companyId) {
        return service.status(companyId);
    }
}
