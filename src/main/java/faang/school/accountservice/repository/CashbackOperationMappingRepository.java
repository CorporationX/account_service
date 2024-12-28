package faang.school.accountservice.repository;

import faang.school.accountservice.model.CashbackOperationMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CashbackOperationMappingRepository extends JpaRepository<CashbackOperationMapping, Long> {
    Optional<CashbackOperationMapping> findByTariffIdAndOperationType(Long tariffId, String operationType);

    List<CashbackOperationMapping> findByTariffId(Long tariffId);

    void deleteByTariffIdAndOperationType(Long tariffId, String operationType);
}
