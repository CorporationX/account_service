package faang.school.accountservice.repository.savings;

import faang.school.accountservice.model.savings.SavingsAccount;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
