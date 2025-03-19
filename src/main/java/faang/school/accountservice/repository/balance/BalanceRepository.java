package faang.school.accountservice.repository.balance;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.non_retryable.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Balance b WHERE b.id = :balanceId")
    Optional<Balance> findByIdForUpdate(@Param("balanceId") UUID balanceId);

    default Balance findByIdForUpdateOrThrow(UUID balanceId) {
        return findByIdForUpdate(balanceId).orElseThrow(() ->
                new EntityNotFoundException("Balance not found for id: " + balanceId));
    }
}
