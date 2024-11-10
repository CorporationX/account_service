package faang.school.accountservice.repository;

import faang.school.accountservice.entity.cashback.OperationCashback;
import faang.school.accountservice.entity.cashback.id.CashbackId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface OperationCashbackRepository extends JpaRepository<OperationCashback, CashbackId> {
    @Transactional(readOnly = true)
    Optional<OperationCashback> findById(CashbackId id);

    @Transactional
    OperationCashback save(OperationCashback operationCashback);

    @Transactional
    @Modifying
    void deleteById(CashbackId id);
}
