package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.SavingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingAccountRepository extends JpaRepository<SavingAccount, Long> {
    List<SavingAccount> findByAccountOwnerUserId(Long id);

    List<SavingAccount> findByAccountOwnerProjectId(Long id);
}
