package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator numberGenerator;

    @Transactional(readOnly = true)
    public Account getAccountById(Long id) {
        return accountRepository.findByIdOrThrow(id);
    }

    @Transactional(readOnly = true)
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumberOrThrow(accountNumber);
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByOwner(Long ownerId, OwnerType ownerType) {
        return accountRepository.findByOwnerIdAndOwnerType(ownerId, ownerType);
    }

    @Transactional
    public Account openAccount(Account account) {
        String accountNumber = generateAccountNumber();
        account.setAccountNumber(accountNumber);
        return accountRepository.save(account);
    }

    @Transactional
    public Account blockAccount(Long id) {
        Account account = accountRepository.findByIdOrThrow(id);
        account.setAccountStatus(AccountStatus.BLOCKED);
        return accountRepository.save(account);
    }

    @Transactional
    public Account closeAccount(Long id) {
        Account account = accountRepository.findByIdOrThrow(id);
        account.setAccountStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        return accountRepository.save(account);
    }


    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = numberGenerator.generate();
        } while (accountRepository.existsAccountByAccountNumber(accountNumber));
        return accountNumber;
    }
}
