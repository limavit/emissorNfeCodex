package com.example.nfe.company;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    List<Company> findByOwnerUserIdOrderByCorporateName(UUID ownerUserId);
    Optional<Company> findByIdAndOwnerUserId(UUID id, UUID ownerUserId);
    boolean existsByOwnerUserIdAndCnpj(UUID ownerUserId, String cnpj);
}
