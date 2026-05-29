package br.com.nfesefassp.repository;

import br.com.nfesefassp.model.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByCompanyIdOrderByDescription(UUID companyId);
    Optional<Product> findByIdAndCompanyId(UUID id, UUID companyId);
    boolean existsByCompanyIdAndInternalCode(UUID companyId, String internalCode);
}
