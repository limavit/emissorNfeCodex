package com.example.nfe.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "company_id")
    private UUID companyId;
    private String action;
    @Column(name = "entity_name")
    private String entityName;
    @Column(name = "entity_id")
    private String entityId;
    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    protected AuditLog() {
    }

    public AuditLog(UUID userId, UUID companyId, String action, String entityName, String entityId) {
        this.userId = userId;
        this.companyId = companyId;
        this.action = action;
        this.entityName = entityName;
        this.entityId = entityId;
    }
}
