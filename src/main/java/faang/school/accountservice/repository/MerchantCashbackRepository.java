package faang.school.accountservice.repository;

import faang.school.accountservice.entity.cashback.MerchantCashback;
import faang.school.accountservice.entity.cashback.id.CashbackId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface MerchantCashbackRepository extends JpaRepository<MerchantCashback, CashbackId> {
    @Transactional(readOnly = true)
    Optional<MerchantCashback> findById(CashbackId id);

    @Transactional
    MerchantCashback save(MerchantCashback merchantCashback);

    @Transactional
    @Modifying
    void deleteById(CashbackId id);
}
