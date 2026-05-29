package com.example.nfe.product;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies/{companyId}/products/{productId}/tax-rules")
public class ProductTaxRuleController {
    private final JdbcTemplate jdbc;

    public ProductTaxRuleController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping
    public List<Map<String, Object>> list(@PathVariable UUID companyId, @PathVariable UUID productId) {
        return jdbc.queryForList("select * from product_tax_rules where company_id = ? and product_id = ? order by valid_from desc", companyId, productId);
    }

    @PostMapping
    public Map<String, Object> create(@PathVariable UUID companyId, @PathVariable UUID productId, @RequestBody Map<String, Object> body) {
        UUID id = UUID.randomUUID();
        jdbc.update("""
                insert into product_tax_rules (id, company_id, product_id, uf_origin, uf_destination, operation_type, tax_regime,
                cfop, icms_cst, icms_csosn, icms_rate, pis_cst, pis_rate, cofins_cst, cofins_rate, valid_from, active)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_date, true)
                """, id, companyId, productId, body.getOrDefault("ufOrigin", "SP"), body.getOrDefault("ufDestination", "SP"),
                body.getOrDefault("operationType", "VENDA"), body.getOrDefault("taxRegime", "REGIME_NORMAL"),
                body.get("cfop"), body.get("icmsCst"), body.get("icmsCsosn"), body.get("icmsRate"),
                body.get("pisCst"), body.get("pisRate"), body.get("cofinsCst"), body.get("cofinsRate"));
        return Map.of("id", id, "companyId", companyId, "productId", productId);
    }
}
