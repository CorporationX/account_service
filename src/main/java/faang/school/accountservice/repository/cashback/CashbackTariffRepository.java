package faang.school.accountservice.repository.cashback;

import faang.school.accountservice.model.cashback.CashbackTariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashbackTariffRepository extends JpaRepository<CashbackTariff, Long> {
    Optional<CashbackTariff> findById(long id);
}