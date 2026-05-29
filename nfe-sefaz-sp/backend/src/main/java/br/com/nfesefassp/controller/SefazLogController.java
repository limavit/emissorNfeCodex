package br.com.nfesefassp.controller;

import br.com.nfesefassp.model.*;
import br.com.nfesefassp.service.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies/{companyId}/sefaz-logs")
public class SefazLogController {
    private final JdbcTemplate jdbc;

    public SefazLogController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping
    public List<Map<String, Object>> list(@PathVariable UUID companyId) {
        return jdbc.queryForList("select * from sefaz_communication_logs where company_id = ? order by started_at desc limit 100", companyId);
    }
}
