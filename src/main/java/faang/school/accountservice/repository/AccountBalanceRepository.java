package faang.school.accountservice.repository;

import faang.school.accountservice.model.AccountBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountBalanceRepository extends JpaRepository<AccountBalance, Long> {
    @Query(value = "SELECT * FROM Balances b WHERE b.account_id = :accountId", nativeQuery = true)
    Optional<AccountBalance> findByAccountId(Long accountId);
}
