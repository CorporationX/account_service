package faang.school.accountservice.repository;

import faang.school.accountservice.model.entity.cashback.CashbackTariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashbackTariffRepository extends JpaRepository<CashbackTariff, Long> {
}
