package br.com.nfesefassp.controller;

import br.com.nfesefassp.model.ProductTaxRule;
import br.com.nfesefassp.model.ProductTaxRuleRequest;
import br.com.nfesefassp.service.ProductTaxRuleService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies/{companyId}/products/{productId}/tax-rules")
public class ProductTaxRuleController {
    private final ProductTaxRuleService service;

    public ProductTaxRuleController(ProductTaxRuleService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductTaxRule> list(@PathVariable UUID companyId, @PathVariable UUID productId) {
        return service.list(companyId, productId);
    }

    @PostMapping
    public ProductTaxRule create(@PathVariable UUID companyId, @PathVariable UUID productId,
                                 @Valid @RequestBody ProductTaxRuleRequest request) {
        return service.create(companyId, productId, request);
    }

    @PutMapping("/{id}")
    public ProductTaxRule update(@PathVariable UUID companyId, @PathVariable UUID productId, @PathVariable UUID id,
                                 @Valid @RequestBody ProductTaxRuleRequest request) {
        return service.update(companyId, productId, id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID companyId, @PathVariable UUID productId, @PathVariable UUID id) {
        service.delete(companyId, productId, id);
    }
}
