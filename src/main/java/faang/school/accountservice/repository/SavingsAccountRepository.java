package faang.school.accountservice.repository;

import faang.school.accountservice.entity.savings_account.SavingsAccount;
import faang.school.accountservice.enums.account.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, UUID> {
    @Query(nativeQuery = true, value = """
            SELECT tr.rate
                FROM tariff_to_savings_account_binding tsab
                JOIN tariff t ON t.id = tsab.tariff_id
                JOIN tariff_rate tr ON tr.tariff_id = t.id
            WHERE tsab.savings_account_id = :savingsAccountId
            ORDER BY tsab.created_at DESC, tr.created_at DESC
            LIMIT 1
            """)
    double getCurrentRate(@Param("savingsAccountId") UUID savingsAccountId);

    List<SavingsAccount> findByAccount_Status(AccountStatus status);

    Optional<SavingsAccount> findByAccountUserId(Long userId);
}
