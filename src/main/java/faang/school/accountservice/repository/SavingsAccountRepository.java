package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {
    Optional<SavingsAccount> findByAccountId(String accountId);

    @Query(value = """
            SELECT CASE WHEN (COUNT(sa) > 0) THEN TRUE ELSE FALSE END
            FROM SavingsAccount sa
            WHERE sa.account = :account
    """)
    boolean existsByAccount(Account account);

    @Query(value = """
            SELECT sa
            FROM SavingsAccount sa
            WHERE sa.account.status = 'ACTIVE'
    """)
    List<SavingsAccount> findAllActiveAccountsForInterestCalculation();
}