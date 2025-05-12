package faang.school.accountservice.repository;

import faang.school.accountservice.entity.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {

    List<SavingsAccount> findAllByLastInterestAccrualAtIsBeforeOrLastInterestAccrualAtIsNull
            (LocalDateTime lastInterestAccrualAt);

    @Query("""
        SELECT th.tariff.typeName
        FROM SavingsAccount sa
        JOIN sa.tariffHistory th
        WHERE sa.accountId = :accountId
        ORDER BY th.appliedAt DESC
        LIMIT 1
    """)
    Optional<String> findLatestTariffTypeNameByAccountId(@Param("accountId") Long accountId);
}
