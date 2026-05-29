package br.com.nfesefassp.controller;

import br.com.nfesefassp.model.*;
import br.com.nfesefassp.service.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    private final CompanyService service;

    public CompanyController(CompanyService service) {
        this.service = service;
    }

    @GetMapping
    public List<Company> list() {
        return service.list();
    }

    @PostMapping
    public Company create(@Valid @RequestBody CompanyRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public Company get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Company update(@PathVariable UUID id, @Valid @RequestBody CompanyRequest request) {
        return service.update(id, request);
    }

    @PostMapping("/{id}/select")
    public Company select(@PathVariable UUID id) {
        return service.select(id);
    }

    @GetMapping("/current")
    public Company current() {
        return service.list().stream().findFirst().orElseThrow(() -> new IllegalStateException("Cadastre uma empresa emissora para começar a emitir NF-e."));
    }
}
