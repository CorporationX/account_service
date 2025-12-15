package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountBalance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountBalanceRepository extends JpaRepository<AccountBalance, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ab FROM AccountBalance ab WHERE ab.accountId = :accountId")
    Optional<AccountBalance> findByAccountIdWithLock(@Param("accountId") Long accountId);

    Optional<AccountBalance> findByAccountId(Long accountId);
}

