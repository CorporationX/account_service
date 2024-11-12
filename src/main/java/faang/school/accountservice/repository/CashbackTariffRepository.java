package faang.school.accountservice.repository;

import faang.school.accountservice.entity.cashback.tariff.CashbackTariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CashbackTariffRepository extends JpaRepository<CashbackTariff, Long> {
    @Transactional(readOnly = true)
    Optional<CashbackTariff> findById(long id);
}
