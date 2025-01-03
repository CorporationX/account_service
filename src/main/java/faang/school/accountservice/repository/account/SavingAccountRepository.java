package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.SavingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingAccountRepository extends JpaRepository<SavingAccount, Long> {
    List<SavingAccount> findByAccountOwnerUserId(Long id);

    List<SavingAccount> findByAccountOwnerProjectId(Long id);

    @Query(nativeQuery = true, value = """
            SELECT sa.* FROM saving_accounts sa
            JOIN account_schema.account ON account_id = account.id
            WHERE account.status = 'ACTIVE'
            AND account.balance > 0
            AND (sa.increased_at < CURRENT_DATE - CAST(:days || ' days' AS INTERVAL) OR sa.increased_at is null)
            """)
    List<SavingAccount> findAllForPayment(@Param("days") int days);

    @Query(nativeQuery = true, value = """
            SELECT * FROM saving_accounts sa
            WHERE account.status = 'ACTIVE'
            AND (sa.bonus_updated_at < CURRENT_DATE - CAST(:days || ' days' AS INTERVAL) OR sa.bonus_updated_at is null)
            """)
    List<SavingAccount> findAllForBonusUpdating(@Param("days") int days);

}
