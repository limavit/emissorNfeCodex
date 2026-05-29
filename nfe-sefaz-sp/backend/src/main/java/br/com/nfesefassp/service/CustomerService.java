package br.com.nfesefassp.service;

import br.com.nfesefassp.model.Customer;
import br.com.nfesefassp.model.CustomerRequest;
import br.com.nfesefassp.repository.CustomerRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {
    private final CustomerRepository customers;

    public CustomerService(CustomerRepository customers) {
        this.customers = customers;
    }

    public List<Customer> list(UUID companyId) {
        return customers.findByCompanyIdOrderByName(companyId);
    }

    public Customer get(UUID companyId, UUID id) {
        return customers.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado."));
    }

    @Transactional
    public Customer create(UUID companyId, CustomerRequest request) {
        return customers.save(Customer.from(companyId, request));
    }

    @Transactional
    public Customer update(UUID companyId, UUID id, CustomerRequest request) {
        Customer customer = get(companyId, id);
        customer.apply(request);
        return customer;
    }

    @Transactional
    public void delete(UUID companyId, UUID id) {
        get(companyId, id).deactivate();
    }
}
