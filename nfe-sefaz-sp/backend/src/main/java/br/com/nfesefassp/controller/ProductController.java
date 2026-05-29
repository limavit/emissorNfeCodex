package br.com.nfesefassp.controller;

import br.com.nfesefassp.model.Product;
import br.com.nfesefassp.model.ProductRequest;
import br.com.nfesefassp.service.ProductService;
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
@RequestMapping("/api/companies/{companyId}/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> list(@PathVariable UUID companyId) {
        return service.list(companyId);
    }

    @PostMapping
    public Product create(@PathVariable UUID companyId, @Valid @RequestBody ProductRequest request) {
        return service.create(companyId, request);
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable UUID companyId, @PathVariable UUID id) {
        return service.get(companyId, id);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable UUID companyId, @PathVariable UUID id, @Valid @RequestBody ProductRequest request) {
        return service.update(companyId, id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID companyId, @PathVariable UUID id) {
        service.delete(companyId, id);
    }
}
