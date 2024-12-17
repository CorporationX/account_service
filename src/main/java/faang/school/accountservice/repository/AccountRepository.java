package faang.school.accountservice.repository;

import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.repository.jpa.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccountRepository {
    private final AccountJpaRepository accountJpaRepository;

    public Account save(Account account) {
        return accountJpaRepository.save(account);
    }

    public boolean isExistsAccount(String accountNumber) {
        return accountJpaRepository.existsByAccountNumber(accountNumber);
    }
}
