package faang.school.accountservice.repository;

import faang.school.accountservice.model.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {
    Optional<SavingsAccount> findByAccountId(Long accountId);
    Optional<SavingsAccount> findByAccountOwnerId(Long ownerId);
}
