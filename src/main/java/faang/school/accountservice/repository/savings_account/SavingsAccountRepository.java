package faang.school.accountservice.repository.savings_account;

import faang.school.accountservice.entity.savings_account.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {

    @Query(value = """
            SELECT savings_account.* FROM savings_account
            JOIN account 
            ON savings_account.account_id = account.id 
            WHERE account.owner_id = :ownerId;
            """, nativeQuery = true)
    List<SavingsAccount> getSavingsAccountsByOwnerId(long ownerId);
}
