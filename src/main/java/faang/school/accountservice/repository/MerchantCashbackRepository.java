package faang.school.accountservice.repository;

import faang.school.accountservice.model.entity.cashback.MerchantCashback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantCashbackRepository extends JpaRepository<MerchantCashback, Long> {
}
