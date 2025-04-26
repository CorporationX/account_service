package faang.school.accountservice.repository;

import faang.school.accountservice.entity.SavingAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavingsAccountRepository extends JpaRepository<SavingAccount, Long> {
    Optional<SavingAccount> findByAccountId(Long accountId);
}