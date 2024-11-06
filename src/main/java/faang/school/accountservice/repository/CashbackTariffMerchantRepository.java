package faang.school.accountservice.repository;

import faang.school.accountservice.model.entity.cashback.CashbackTariffMerchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashbackTariffMerchantRepository extends JpaRepository<CashbackTariffMerchant, Long> {
}
