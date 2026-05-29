package com.example.nfe.certificate;

import com.example.nfe.audit.AuditService;
import com.example.nfe.security.CurrentUser;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CertificateService {
    private final JdbcTemplate jdbc;
    private final AuditService auditService;

    public CertificateService(JdbcTemplate jdbc, AuditService auditService) {
        this.jdbc = jdbc;
        this.auditService = auditService;
    }

    public Map<String, Object> upload(UUID companyId, MultipartFile file, String password) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Certificado obrigatorio.");
        }
        if (!file.getOriginalFilename().endsWith(".pfx") && !file.getOriginalFilename().endsWith(".p12")) {
            throw new IllegalArgumentException("Use certificado A1 .pfx ou .p12.");
        }
        // MVP: metadata and encrypted storage hook. Real parsing is isolated here.
        auditService.register(CurrentUser.id(), companyId, "UPLOAD_CERTIFICATE", "DigitalCertificate", companyId.toString());
        return Map.of(
                "companyId", companyId,
                "status", "RECEIVED_FOR_VALIDATION",
                "validUntil", OffsetDateTime.now().plusDays(90).toString(),
                "warning", "Persistencia criptografada e leitura PKCS12 devem ser habilitadas antes de producao.");
    }

    public Map<String, Object> status(UUID companyId) {
        return Map.of("companyId", companyId, "status", "AUSENTE");
    }

    public void remove(UUID companyId) {
        auditService.register(CurrentUser.id(), companyId, "DELETE_CERTIFICATE", "DigitalCertificate", companyId.toString());
    }
}
