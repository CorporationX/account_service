package faang.school.accountservice.repository;

import faang.school.accountservice.entity.CashbackOperationMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CashbackOperationMappingRepository extends JpaRepository<CashbackOperationMapping, Long> {
    List<CashbackOperationMapping> findByCashbackTariffId(Long cashbackTariffId);
}
