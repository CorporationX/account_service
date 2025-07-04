package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    @Query(value = "INSERT INTO balance (account_number, authorization_balance, actual_balance) " +
            "VALUES (:accountNumber, COALESCE(:authorizationBalance, 0), COALESCE(:actualBalance, 0)) returning id",
            nativeQuery = true)
    Long create(@Param("accountNumber") String accountNumber,
                       @Param("authorizationBalance") BigDecimal authorizationBalance,
                       @Param("actualBalance") BigDecimal actualBalance);
}
