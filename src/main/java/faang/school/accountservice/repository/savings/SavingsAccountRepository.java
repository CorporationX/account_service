package faang.school.accountservice.repository.savings;

import faang.school.accountservice.model.savings.SavingsAccount;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {

  @Query(nativeQuery = true, value = """
      INSERT INTO savings_account (account_id, tariff_history, created_at, updated_at)
      VALUES (?1, ?2, NOW(), NOW()) RETURNING *
      """)
  SavingsAccount create(Long accountId, String tariff);

  List<SavingsAccount> findAllByAccountOwnerId(Long ownerId);

  @Query(nativeQuery = true, value = """
      SELECT savings.id as id, savings.last_income_at as lastIncomeDate, b.id as balanceId,
      replace(t.rate_history, '%', '') \\:\\: json -> -1 as currentRate
        FROM (
          SELECT s.*, cast(s.tariff_history \\:\\: json ->> -1 as bigint) as current_tariff
          FROM savings_account s
          JOIN account a on a.id = s.account_id
        ) as savings
      JOIN tariff t on savings.current_tariff = t.id AND savings.last_income_at < CURRENT_DATE
      JOIN balance b on b.account_id = savings.account_id
      """)
  List<SavingsAccountToPay> getSavingsWithRates();

  interface SavingsAccountToPay {

    Long getId();

    LocalDate getLastIncomeDate();

    Long getBalanceId();

    BigDecimal getCurrentRate();
  }

  @Transactional
  @Modifying
  @Query(nativeQuery = true, value = """
      UPDATE balance SET version = version + 1,
      actual_value = authorized_value * (1 + :rate/100),
      authorized_value = authorized_value * (1 + :rate/100)
      WHERE id = :balanceId
      """)
  void updateBalanceByIncome(Long balanceId, BigDecimal rate);

  @Transactional
  @Modifying
  @Query(nativeQuery = true, value = """
      UPDATE savings_account SET version = version + 1,
      last_income_at = CURRENT_DATE
      WHERE id = :savingsAccountId
      """)
  void updateSavingsAccountAfterIncome(Long savingsAccountId);
}
