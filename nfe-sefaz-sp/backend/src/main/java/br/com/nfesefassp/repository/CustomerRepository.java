package br.com.nfesefassp.repository;

import br.com.nfesefassp.model.Customer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findByCompanyIdOrderByName(UUID companyId);
    Optional<Customer> findByIdAndCompanyId(UUID id, UUID companyId);
}
