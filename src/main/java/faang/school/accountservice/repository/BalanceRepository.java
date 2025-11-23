package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Balance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface BalanceRepository extends JpaRepository<Balance, Long> {

    Optional<Balance> findByAccountId(UUID accountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Balance b where b.account.id = :accountId")
    Optional<Balance> findByAccountIdForUpdate(UUID accountId);
}
