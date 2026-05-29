package br.com.nfesefassp.repository;

import br.com.nfesefassp.model.ProductTaxRule;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTaxRuleRepository extends JpaRepository<ProductTaxRule, UUID> {
    List<ProductTaxRule> findByCompanyIdAndProductIdOrderByValidFromDesc(UUID companyId, UUID productId);
    Optional<ProductTaxRule> findByIdAndCompanyIdAndProductId(UUID id, UUID companyId, UUID productId);
}
