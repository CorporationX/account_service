package faang.school.accountservice.repository;

import faang.school.accountservice.entity.CashbackTariff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashbackTariffRepository extends JpaRepository<CashbackTariff, Long> {
}

