package faang.school.accountservice.repository.balance;

import faang.school.accountservice.model.balance.Balance;
import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {

    @Query(nativeQuery = true, value = """
      INSERT INTO balance (account_id, actual_value, created_at, updated_at)
      VALUES (?1, ?2, NOW(), NOW()) RETURNING *
      """)
    Balance create(long accountId, BigDecimal value);

    @Query("SELECT b.id FROM Balance b WHERE b.account.id = :accountId")
    Long findLongByAccountId(@Param("accountId") Long accountId);

    Balance findByAccountId(Long accountId);
}