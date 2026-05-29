package br.com.nfesefassp.service;

import br.com.nfesefassp.controller.AuthController;
import br.com.nfesefassp.model.*;
import br.com.nfesefassp.repository.*;
import br.com.nfesefassp.security.*;
import br.com.nfesefassp.util.*;

import br.com.nfesefassp.service.AuditService;
import br.com.nfesefassp.util.CnpjValidator;
import br.com.nfesefassp.security.CurrentUser;
import br.com.nfesefassp.service.StorageService;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CertificateService {
    private static final Pattern CNPJ_PATTERN = Pattern.compile("(?<!\\d)(\\d{14})(?!\\d)");
    private static final int GCM_TAG_BITS = 128;
    private static final int GCM_IV_BYTES = 12;

    private final JdbcTemplate jdbc;
    private final AuditService auditService;
    private final StorageService storageService;
    private final SecretKeySpec encryptionKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public CertificateService(JdbcTemplate jdbc, AuditService auditService, StorageService storageService,
                              @Value("${app.certificate-encryption-key}") String encryptionKey) {
        this.jdbc = jdbc;
        this.auditService = auditService;
        this.storageService = storageService;
        this.encryptionKey = new SecretKeySpec(sha256(encryptionKey), "AES");
    }

    @Transactional
    public Map<String, Object> upload(UUID companyId, MultipartFile file, String password) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Certificado obrigatorio.");
        }
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (!filename.endsWith(".pfx") && !filename.endsWith(".p12")) {
            throw new IllegalArgumentException("Use certificado A1 .pfx ou .p12.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Senha do certificado obrigatoria.");
        }

        try {
            byte[] rawFile = file.getBytes();
            CertificateMetadata metadata = readPkcs12(rawFile, password.toCharArray());
            Map<String, Object> company = jdbc.queryForMap("select cnpj from companies where id = ?", companyId);
            String companyCnpj = CnpjValidator.digits(String.valueOf(company.get("cnpj")));

            if (metadata.document() == null || metadata.document().isBlank()) {
                throw new IllegalArgumentException("Nao foi possivel identificar o CNPJ no certificado.");
            }
            if (!metadata.document().equals(companyCnpj)) {
                throw new IllegalArgumentException("O CNPJ do certificado nao corresponde ao CNPJ da empresa selecionada.");
            }
            if (metadata.validUntil().isBefore(OffsetDateTime.now())) {
                throw new IllegalArgumentException("Certificado vencido.");
            }

            byte[] encryptedFile = encryptBytes(rawFile);
            String encryptedPassword = encryptString(password);
            String relativePath = "certificates/" + companyId + "/" + UUID.randomUUID() + ".p12.enc";
            String path = storageService.saveBytes(relativePath, encryptedFile);

            jdbc.update("update digital_certificates set active = false, updated_at = now() where company_id = ? and active = true", companyId);
            UUID certificateId = UUID.randomUUID();
            jdbc.update("""
                    insert into digital_certificates (id, company_id, encrypted_file_path, encrypted_password, subject_name,
                    issuer_name, serial_number, valid_from, valid_until, certificate_document, active)
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, true)
                    """, certificateId, companyId, path, encryptedPassword, metadata.subjectName(), metadata.issuerName(),
                    metadata.serialNumber(), metadata.validFrom(), metadata.validUntil(), metadata.document());

            auditService.register(CurrentUser.id(), companyId, "UPLOAD_CERTIFICATE", "DigitalCertificate", certificateId.toString());
            return response(certificateId, companyId, metadata, certificateStatus(metadata.validUntil()));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Nao foi possivel abrir o certificado. Verifique arquivo e senha.");
        }
    }

    public Map<String, Object> status(UUID companyId) {
        return jdbc.query("""
                select id, subject_name, issuer_name, serial_number, valid_from, valid_until, certificate_document
                from digital_certificates
                where company_id = ? and active = true
                order by created_at desc
                limit 1
                """, rs -> {
            if (!rs.next()) {
                return Map.of("companyId", companyId, "status", "AUSENTE");
            }
            OffsetDateTime validUntil = rs.getObject("valid_until", OffsetDateTime.class);
            return Map.of(
                    "id", rs.getObject("id", UUID.class),
                    "companyId", companyId,
                    "status", certificateStatus(validUntil),
                    "subjectName", safe(rs.getString("subject_name")),
                    "issuerName", safe(rs.getString("issuer_name")),
                    "serialNumber", safe(rs.getString("serial_number")),
                    "validFrom", rs.getObject("valid_from", OffsetDateTime.class),
                    "validUntil", validUntil,
                    "certificateDocument", safe(rs.getString("certificate_document")));
        }, companyId);
    }

    @Transactional
    public void remove(UUID companyId) {
        jdbc.update("update digital_certificates set active = false, updated_at = now() where company_id = ? and active = true", companyId);
        auditService.register(CurrentUser.id(), companyId, "DELETE_CERTIFICATE", "DigitalCertificate", companyId.toString());
    }

    public SigningCertificate loadSigningCertificate(UUID companyId) {
        Map<String, Object> row = jdbc.query("""
                select encrypted_file_path, encrypted_password, valid_until
                from digital_certificates
                where company_id = ? and active = true
                order by created_at desc
                limit 1
                """, rs -> {
            if (!rs.next()) {
                return null;
            }
            return Map.of(
                    "encrypted_file_path", rs.getString("encrypted_file_path"),
                    "encrypted_password", rs.getString("encrypted_password"),
                    "valid_until", rs.getObject("valid_until", OffsetDateTime.class));
        }, companyId);
        if (row == null) {
            throw new IllegalStateException("Empresa sem certificado digital A1 ativo.");
        }
        OffsetDateTime validUntil = (OffsetDateTime) row.get("valid_until");
        if (validUntil == null || validUntil.isBefore(OffsetDateTime.now())) {
            throw new IllegalStateException("Certificado digital vencido.");
        }

        try {
            byte[] pkcs12 = decryptBytes(storageService.readBytes(String.valueOf(row.get("encrypted_file_path"))));
            char[] password = decryptString(String.valueOf(row.get("encrypted_password"))).toCharArray();
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new ByteArrayInputStream(pkcs12), password);

            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (!keyStore.isKeyEntry(alias)) {
                    continue;
                }
                PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password);
                X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
                if (privateKey != null && certificate != null) {
                    return new SigningCertificate(privateKey, certificate);
                }
            }
            throw new IllegalStateException("Certificado A1 sem chave privada disponivel.");
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Nao foi possivel carregar o certificado digital para assinatura.", e);
        }
    }

    private CertificateMetadata readPkcs12(byte[] rawFile, char[] password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new ByteArrayInputStream(rawFile), password);

        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (!keyStore.isKeyEntry(alias)) {
                continue;
            }
            X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
            if (certificate == null) {
                continue;
            }
            String subject = certificate.getSubjectX500Principal().getName(X500Principal.RFC2253);
            String issuer = certificate.getIssuerX500Principal().getName(X500Principal.RFC2253);
            return new CertificateMetadata(
                    subject,
                    issuer,
                    certificate.getSerialNumber().toString(16).toUpperCase(),
                    certificate.getNotBefore().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime(),
                    certificate.getNotAfter().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime(),
                    extractCnpj(certificate, subject));
        }
        throw new IllegalArgumentException("Certificado A1 sem chave privada.");
    }

    private String extractCnpj(X509Certificate certificate, String subject) {
        String fromSubject = firstValidCnpj(subject);
        if (fromSubject != null) {
            return fromSubject;
        }
        try {
            Collection<List<?>> names = certificate.getSubjectAlternativeNames();
            if (names == null) {
                return null;
            }
            for (List<?> name : names) {
                if (name.size() < 2 || name.get(1) == null) {
                    continue;
                }
                Object value = name.get(1);
                String candidate = value instanceof byte[] bytes
                        ? firstValidCnpj(new String(bytes, StandardCharsets.ISO_8859_1))
                        : firstValidCnpj(String.valueOf(value));
                if (candidate != null) {
                    return candidate;
                }
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
    }

    private String firstValidCnpj(String value) {
        Matcher matcher = CNPJ_PATTERN.matcher(value == null ? "" : value);
        while (matcher.find()) {
            String candidate = matcher.group(1);
            if (CnpjValidator.isValid(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private String certificateStatus(OffsetDateTime validUntil) {
        OffsetDateTime now = OffsetDateTime.now();
        if (validUntil == null) {
            return "AUSENTE";
        }
        if (validUntil.isBefore(now)) {
            return "VENCIDO";
        }
        if (validUntil.isBefore(now.plusDays(30))) {
            return "PROXIMO_VENCIMENTO";
        }
        return "VALIDO";
    }

    private Map<String, Object> response(UUID certificateId, UUID companyId, CertificateMetadata metadata, String status) {
        return Map.of(
                "id", certificateId,
                "companyId", companyId,
                "status", status,
                "subjectName", metadata.subjectName(),
                "issuerName", metadata.issuerName(),
                "serialNumber", metadata.serialNumber(),
                "validFrom", metadata.validFrom(),
                "validUntil", metadata.validUntil(),
                "certificateDocument", metadata.document());
    }

    private byte[] encryptBytes(byte[] plain) throws Exception {
        byte[] iv = new byte[GCM_IV_BYTES];
        secureRandom.nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
        byte[] encrypted = cipher.doFinal(plain);
        return ByteBuffer.allocate(iv.length + encrypted.length).put(iv).put(encrypted).array();
    }

    private String encryptString(String plain) throws Exception {
        return Base64.getEncoder().encodeToString(encryptBytes(plain.getBytes(StandardCharsets.UTF_8)));
    }

    private byte[] decryptBytes(byte[] encrypted) throws Exception {
        if (encrypted.length <= GCM_IV_BYTES) {
            throw new IllegalArgumentException("Conteudo criptografado invalido.");
        }
        ByteBuffer buffer = ByteBuffer.wrap(encrypted);
        byte[] iv = new byte[GCM_IV_BYTES];
        buffer.get(iv);
        byte[] cipherText = new byte[buffer.remaining()];
        buffer.get(cipherText);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, encryptionKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
        return cipher.doFinal(cipherText);
    }

    private String decryptString(String encrypted) throws Exception {
        return new String(decryptBytes(Base64.getDecoder().decode(encrypted)), StandardCharsets.UTF_8);
    }

    private byte[] sha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao preparar chave de criptografia.", e);
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private record CertificateMetadata(String subjectName, String issuerName, String serialNumber,
                                       OffsetDateTime validFrom, OffsetDateTime validUntil, String document) {
    }

    public record SigningCertificate(PrivateKey privateKey, X509Certificate certificate) {
    }
}
