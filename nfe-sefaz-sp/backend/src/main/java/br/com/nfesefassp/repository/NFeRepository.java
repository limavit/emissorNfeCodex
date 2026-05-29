package br.com.nfesefassp.repository;

import br.com.nfesefassp.model.NFe;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NFeRepository extends JpaRepository<NFe, UUID> {
    List<NFe> findByCompanyIdOrderByCreatedAtDesc(UUID companyId);
    Optional<NFe> findByIdAndCompanyId(UUID id, UUID companyId);
}
