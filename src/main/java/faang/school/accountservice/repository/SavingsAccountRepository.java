package faang.school.accountservice.repository;

import faang.school.accountservice.model.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Evgenii Malkov
 */
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {

    @Query(nativeQuery = true, value = """
            SELECT sa.* FROM savings_account sa
            JOIN account ON account_id = account.id
            WHERE account.owner_user_id = :clientId
            AND account.account_type = :accountType
            """)
    List<SavingsAccount> findByClientIdAndType(long clientId, String accountType);

    SavingsAccount findSavingsAccountByAccountId(long accountId);

    @Query(nativeQuery = true, value = """
            SELECT sa.* FROM savings_account sa
            JOIN account ON account_id = account.id
            WHERE account.account_status = :status
            AND account.account_type = :type
            AND sa.last_success_percent_date < CURRENT_DATE
            """)
    List<SavingsAccount> findSavingsAccountsForPayment(String status, String type);
}
