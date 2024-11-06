package faang.school.accountservice.repository;

import faang.school.accountservice.model.entity.cashback.CashbackTariffOperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashbackTariffOperationTypeRepository extends JpaRepository<CashbackTariffOperationType, Long> {
}
