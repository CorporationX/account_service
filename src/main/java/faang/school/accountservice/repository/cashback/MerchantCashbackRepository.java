package faang.school.accountservice.repository.cashback;

import faang.school.accountservice.model.cashback.CashbackId;
import faang.school.accountservice.model.cashback.MerchantCashback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantCashbackRepository extends JpaRepository<MerchantCashback, CashbackId> {
}