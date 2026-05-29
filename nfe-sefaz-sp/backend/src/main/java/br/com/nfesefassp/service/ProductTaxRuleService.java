package br.com.nfesefassp.service;

import br.com.nfesefassp.model.ProductTaxRule;
import br.com.nfesefassp.model.ProductTaxRuleRequest;
import br.com.nfesefassp.repository.ProductRepository;
import br.com.nfesefassp.repository.ProductTaxRuleRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductTaxRuleService {
    private final ProductTaxRuleRepository taxRules;
    private final ProductRepository products;

    public ProductTaxRuleService(ProductTaxRuleRepository taxRules, ProductRepository products) {
        this.taxRules = taxRules;
        this.products = products;
    }

    public List<ProductTaxRule> list(UUID companyId, UUID productId) {
        assertProductBelongsToCompany(companyId, productId);
        return taxRules.findByCompanyIdAndProductIdOrderByValidFromDesc(companyId, productId);
    }

    @Transactional
    public ProductTaxRule create(UUID companyId, UUID productId, ProductTaxRuleRequest request) {
        assertProductBelongsToCompany(companyId, productId);
        return taxRules.save(ProductTaxRule.from(companyId, productId, request));
    }

    @Transactional
    public ProductTaxRule update(UUID companyId, UUID productId, UUID id, ProductTaxRuleRequest request) {
        ProductTaxRule rule = taxRules.findByIdAndCompanyIdAndProductId(id, companyId, productId)
                .orElseThrow(() -> new IllegalArgumentException("Regra fiscal nao encontrada."));
        rule.apply(request);
        return rule;
    }

    @Transactional
    public void delete(UUID companyId, UUID productId, UUID id) {
        ProductTaxRule rule = taxRules.findByIdAndCompanyIdAndProductId(id, companyId, productId)
                .orElseThrow(() -> new IllegalArgumentException("Regra fiscal nao encontrada."));
        rule.deactivate();
    }

    private void assertProductBelongsToCompany(UUID companyId, UUID productId) {
        products.findByIdAndCompanyId(productId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado."));
    }
}
