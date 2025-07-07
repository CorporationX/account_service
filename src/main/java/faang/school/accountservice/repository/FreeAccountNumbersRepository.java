package faang.school.accountservice.repository;

import faang.school.accountservice.model.AccountBalanceType;
import faang.school.accountservice.model.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM free_account_numbers WHERE account_balance_type = :accountBalanceType RETURNING *", nativeQuery = true)
    Optional<FreeAccountNumber> getFirstFreeAccNumber(@Param("accountBalanceType") AccountBalanceType accountBalanceType);
}