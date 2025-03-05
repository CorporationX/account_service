package faang.school.accountservice.repository;

import faang.school.accountservice.model.savings_account.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {
    @Query(nativeQuery = true, value = """
    SELECT * FROM savings_account
    WHERE account_id = :accountId
    """)
    Optional<SavingsAccount> findByAccountId(long accountId);
}