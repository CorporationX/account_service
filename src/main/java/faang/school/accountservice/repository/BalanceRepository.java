package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Balance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findByAccountId(Long accountId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT b FROM Balance b WHERE b.id = :id")
    Optional<Balance> findByIdWithOptimisticLock(@Param("id") Long id);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT b FROM Balance b WHERE b.account.id = :accountId")
    Optional<Balance> findByAccountIdWithOptimisticLock(@Param("accountId") Long accountId);

    boolean existsByAccountId(Long accountId);
}
