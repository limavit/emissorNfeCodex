package br.com.nfesefassp.controller;

import br.com.nfesefassp.model.*;
import br.com.nfesefassp.service.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies/{companyId}/audit-logs")
public class AuditController {
    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public List<AuditLog> list(@PathVariable UUID companyId) {
        return auditService.recent(companyId);
    }
}
