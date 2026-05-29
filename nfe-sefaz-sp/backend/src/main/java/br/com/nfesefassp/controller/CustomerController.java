package br.com.nfesefassp.controller;

import br.com.nfesefassp.model.Customer;
import br.com.nfesefassp.model.CustomerRequest;
import br.com.nfesefassp.service.CustomerService;
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
@RequestMapping("/api/companies/{companyId}/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public List<Customer> list(@PathVariable UUID companyId) {
        return service.list(companyId);
    }

    @PostMapping
    public Customer create(@PathVariable UUID companyId, @Valid @RequestBody CustomerRequest request) {
        return service.create(companyId, request);
    }

    @GetMapping("/{id}")
    public Customer get(@PathVariable UUID companyId, @PathVariable UUID id) {
        return service.get(companyId, id);
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable UUID companyId, @PathVariable UUID id, @Valid @RequestBody CustomerRequest request) {
        return service.update(companyId, id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID companyId, @PathVariable UUID id) {
        service.delete(companyId, id);
    }
}
