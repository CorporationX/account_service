package faang.school.accountservice.repository;

import faang.school.accountservice.entity.balance.Balance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findByAccountId(UUID accountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Balance b WHERE b.account.id = :accountId")
    Optional<Balance> findAndLockByAccountId(@Param("accountId") UUID accountId);
}