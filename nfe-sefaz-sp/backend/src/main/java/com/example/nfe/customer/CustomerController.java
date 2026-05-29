package com.example.nfe.customer;

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
@RequestMapping("/api/companies/{companyId}/customers")
public class CustomerController {
    private final JdbcTemplate jdbc;

    public CustomerController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping
    public List<Map<String, Object>> list(@PathVariable UUID companyId) {
        return jdbc.queryForList("select * from customers where company_id = ? order by name", companyId);
    }

    @PostMapping
    public Map<String, Object> create(@PathVariable UUID companyId, @RequestBody Map<String, Object> body) {
        UUID id = UUID.randomUUID();
        jdbc.update("""
                insert into customers (id, company_id, person_type, cpf, cnpj, foreign_id, name, trade_name,
                state_registration_indicator, state_registration, email, phone, zip_code, street, number, district,
                city_code_ibge, city_name, uf, active)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, true)
                """, id, companyId, body.getOrDefault("personType", "JURIDICA"), body.get("cpf"), body.get("cnpj"),
                body.get("foreignId"), body.get("name"), body.get("tradeName"),
                body.getOrDefault("stateRegistrationIndicator", "NAO_CONTRIBUINTE"), body.get("stateRegistration"),
                body.get("email"), body.get("phone"), body.get("zipCode"), body.get("street"), body.get("number"),
                body.get("district"), body.get("cityCodeIbge"), body.get("cityName"), body.get("uf"));
        return Map.of("id", id, "companyId", companyId);
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable UUID companyId, @PathVariable UUID id) {
        return jdbc.queryForMap("select * from customers where company_id = ? and id = ?", companyId, id);
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable UUID companyId, @PathVariable UUID id, @RequestBody Map<String, Object> body) {
        jdbc.update("update customers set name = ?, active = coalesce(?, active), updated_at = now() where company_id = ? and id = ?",
                body.get("name"), body.get("active"), companyId, id);
        return get(companyId, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID companyId, @PathVariable UUID id) {
        jdbc.update("update customers set active = false where company_id = ? and id = ?", companyId, id);
    }
}
