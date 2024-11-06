package faang.school.accountservice.repository;

import faang.school.accountservice.entity.MerchantCashback;
import faang.school.accountservice.entity.id.MerchantCashbackId;
import faang.school.accountservice.entity.id.OperationCashbackId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface MerchantCashbackRepository extends JpaRepository<MerchantCashback, MerchantCashbackId> {
    @Transactional(readOnly = true)
    Optional<MerchantCashback> findById(MerchantCashbackId id);

    @Transactional
    MerchantCashback save(MerchantCashback merchantCashback);

    @Transactional
    void deleteById(MerchantCashbackId id);
}
