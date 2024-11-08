package faang.school.accountservice.repository;

import faang.school.accountservice.dto.CashbackTariffDto;
import faang.school.accountservice.entity.cashback.tariff.CashbackTariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CashbackTariffRepository extends JpaRepository<CashbackTariff, Long> {
    @Transactional
    CashbackTariff save(CashbackTariffDto cashbackTariffDto);

    @Transactional(readOnly = true)
    Optional<CashbackTariff> findById(long id);

    @Transactional
    @Modifying
    void deleteById(long id);
}
