package faang.school.accountservice.repository;

import faang.school.accountservice.model.AccountBalanceType;
import faang.school.accountservice.model.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, Long> {

    @Query(value = "FROM FreeAccountNumber WHERE accountBalanceType = :accountBalanceType ORDER BY id LIMIT 1")
    FreeAccountNumber findRandomByAccountBalanceType(@Param("accountBalanceType") AccountBalanceType accountBalanceType);

    @Query(value = "SELECT COUNT(*) FROM FreeAccountNumber WHERE accountBalanceType = :accountBalanceType")
    Integer getActualFreeNumCountByType(@Param("accountBalanceType") AccountBalanceType accountBalanceType);
}