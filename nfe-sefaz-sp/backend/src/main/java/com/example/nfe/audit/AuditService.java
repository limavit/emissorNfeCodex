package com.example.nfe.audit;

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
