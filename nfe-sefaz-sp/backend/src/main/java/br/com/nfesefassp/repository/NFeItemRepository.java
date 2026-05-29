package br.com.nfesefassp.repository;

import br.com.nfesefassp.model.NFeItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NFeItemRepository extends JpaRepository<NFeItem, UUID> {
    List<NFeItem> findByNfeIdOrderByItemNumber(UUID nfeId);
    long countByNfeId(UUID nfeId);
    void deleteByNfeId(UUID nfeId);
}
