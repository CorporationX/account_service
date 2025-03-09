package faang.school.accountservice.repository;

import faang.school.accountservice.dto.savingsAccounts.SavingsAccountReadDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {
    Optional<SavingsAccount> findSavingsAccountByAccount(Account account);

    Optional<SavingsAccount> findByAccount_AuthorId(Long authorId);
}
