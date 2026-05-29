package br.com.nfesefassp.repository;

import br.com.nfesefassp.model.*;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findTop100ByCompanyIdOrderByCreatedAtDesc(UUID companyId);
}
