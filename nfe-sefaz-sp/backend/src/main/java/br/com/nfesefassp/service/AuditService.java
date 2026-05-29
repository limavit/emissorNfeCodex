package br.com.nfesefassp.service;

import br.com.nfesefassp.controller.AuthController;
import br.com.nfesefassp.model.*;
import br.com.nfesefassp.repository.*;
import br.com.nfesefassp.security.*;
import br.com.nfesefassp.util.*;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void register(UUID userId, UUID companyId, String action, String entityName, String entityId) {
        repository.save(new AuditLog(userId, companyId, action, entityName, entityId));
    }

    public List<AuditLog> recent(UUID companyId) {
        return repository.findTop100ByCompanyIdOrderByCreatedAtDesc(companyId);
    }
}
