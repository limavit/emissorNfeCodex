package br.com.nfesefassp.service;

import br.com.nfesefassp.model.Product;
import br.com.nfesefassp.model.ProductRequest;
import br.com.nfesefassp.repository.ProductRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository products;

    public ProductService(ProductRepository products) {
        this.products = products;
    }

    public List<Product> list(UUID companyId) {
        return products.findByCompanyIdOrderByDescription(companyId);
    }

    public Product get(UUID companyId, UUID id) {
        return products.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado."));
    }

    @Transactional
    public Product create(UUID companyId, ProductRequest request) {
        if (products.existsByCompanyIdAndInternalCode(companyId, request.internalCode())) {
            throw new IllegalArgumentException("Codigo interno ja cadastrado para esta empresa.");
        }
        return products.save(Product.from(companyId, request));
    }

    @Transactional
    public Product update(UUID companyId, UUID id, ProductRequest request) {
        Product product = get(companyId, id);
        product.apply(request);
        return product;
    }

    @Transactional
    public void delete(UUID companyId, UUID id) {
        get(companyId, id).deactivate();
    }
}
