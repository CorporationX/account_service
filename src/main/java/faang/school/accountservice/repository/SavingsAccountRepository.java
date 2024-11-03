package faang.school.accountservice.repository;

import faang.school.accountservice.model.dto.SavingsAccountDto;
import faang.school.accountservice.model.entity.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {

    @Query(value = "SELECT * FROM savings_account sa WHERE sa.account_number IN :accountNumbers", nativeQuery = true)
    List<SavingsAccount> findSaIdsByAccountNumbers(@Param("accountNumbers") List<String> accountNumbers);

    @Query(value = "SELECT new faang.school.accountservice.model.dto.SavingsAccountDto(sa.id, sa.account.id, " +
            "sar.tariff.id, sar.rate, sa.lastDatePercent, sa.createdAt, sa.updatedAt) " +
            "FROM SavingsAccount sa " +
            "LEFT JOIN TariffHistory th ON th.savingsAccount.id = sa.id " +
            "LEFT JOIN SavingsAccountRate sar ON sar.tariff.id = th.tariff.id " +
            "WHERE sa.id  = :id ORDER BY sar.createdAt DESC LIMIT 1")
    SavingsAccountDto findSavingsAccountWithDetails(@Param("id") Long id);

    Optional<SavingsAccount> findByUserId(long userId);
}
