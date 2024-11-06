package faang.school.accountservice.repository;

import faang.school.accountservice.entity.OperationCashback;
import faang.school.accountservice.entity.id.OperationCashbackId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface OperationCashbackRepository extends JpaRepository<OperationCashback, OperationCashbackId> {
    @Transactional(readOnly = true)
    Optional<OperationCashback> findById(OperationCashbackId id);

    @Transactional
    OperationCashback save(OperationCashback operationCashback);

    @Transactional
    void deleteById(OperationCashbackId id);
}
