package com.example.nfe.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies/{companyId}/products")
public class ProductController {
    private final JdbcTemplate jdbc;

    public ProductController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping
    public List<Map<String, Object>> list(@PathVariable UUID companyId) {
        return jdbc.queryForList("select * from products where company_id = ? order by description", companyId);
    }

    @PostMapping
    public Map<String, Object> create(@PathVariable UUID companyId, @RequestBody Map<String, Object> body) {
        UUID id = UUID.randomUUID();
        jdbc.update("""
                insert into products (id, company_id, internal_code, ean, description, ncm, cest, cfop_internal,
                cfop_interstate, cfop_external, commercial_unit, taxable_unit, conversion_factor, unit_price, origin, item_type, active)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, true)
                """, id, companyId, body.get("internalCode"), body.get("ean"), body.get("description"), body.get("ncm"),
                body.get("cest"), body.get("cfopInternal"), body.get("cfopInterstate"), body.get("cfopExternal"),
                body.getOrDefault("commercialUnit", "UN"), body.getOrDefault("taxableUnit", "UN"),
                decimal(body.get("conversionFactor"), BigDecimal.ONE), decimal(body.get("unitPrice"), BigDecimal.ZERO),
                body.getOrDefault("origin", "0"), body.getOrDefault("itemType", "MERCADORIA_REVENDA"));
        return Map.of("id", id, "companyId", companyId);
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable UUID companyId, @PathVariable UUID id) {
        return jdbc.queryForMap("select * from products where company_id = ? and id = ?", companyId, id);
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable UUID companyId, @PathVariable UUID id, @RequestBody Map<String, Object> body) {
        jdbc.update("update products set description = ?, unit_price = ?, updated_at = now() where company_id = ? and id = ?",
                body.get("description"), decimal(body.get("unitPrice"), BigDecimal.ZERO), companyId, id);
        return get(companyId, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID companyId, @PathVariable UUID id) {
        jdbc.update("update products set active = false where company_id = ? and id = ?", companyId, id);
    }

    private BigDecimal decimal(Object value, BigDecimal fallback) {
        return value == null ? fallback : new BigDecimal(value.toString());
    }
}
