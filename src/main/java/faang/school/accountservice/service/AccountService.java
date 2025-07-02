package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.common.RecordNotFoundException;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.account.AccountValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final FreeAccountNumbersService freeAccountNumbersService;
    private final AccountRepository accountRepository;

    @Transactional
    public Account createAccount(Account account) {
        String accountNumber = freeAccountNumbersService.generate();

        account.setAccountNumber(accountNumber);
        account.setStatus(AccountStatus.ACTIVE);

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Account getAccountById(UUID accountId) {
        return getExistingAccount(accountId);
    }

    @Transactional(readOnly = true)
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format("Account with number %s was not found", accountNumber)
                ));
    }

    @Transactional
    public Account closeAccount(UUID accountId) {
        Account account = getExistingAccount(accountId);
        AccountValidator.validateAccountNotClosed(account);

        account.setStatus(AccountStatus.CLOSED);
        account.setUpdatedAt(LocalDateTime.now());
        account.setClosedAt(LocalDateTime.now());

        return accountRepository.save(account);
    }

    @Transactional
    public Account convertAccountCurrency(UUID accountId, Currency currency) {
        Account account = getExistingAccount(accountId);
        AccountValidator.validateAccountNotClosed(account);
        account.setCurrency(currency);

        return accountRepository.save(account);
    }


    public Account getExistingAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format("Account with id %s was not found", accountId)
                ));
    }
}