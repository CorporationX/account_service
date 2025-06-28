package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.AccountStatus;
import faang.school.accountservice.entity.currency.Currency;
import faang.school.accountservice.exception.account.AccountNotFoundException;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.service.currency.CurrencyService;
import faang.school.accountservice.validation.account.AccountValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountValidator accountValidator;
    private final AccountNumberGenerator accountNumberGenerator;
    private final CurrencyService currencyService;

    @Transactional(readOnly = true)
    public Account getAccountById(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> getAccountNotFound(accountId));
    }

    @Transactional
    public Account createAccount(Account account, UUID currencyId) {
        String number = accountNumberGenerator.generateAccountNumber();
        account.setNumber(number);

        Currency currency = currencyService.getCurrencyById(currencyId);
        account.setCurrency(currency);

        Account savedAccount = accountRepository.save(account);
        log.info("Account {} has been saved", savedAccount);

        return savedAccount;
    }

    @Transactional
    public Account blockAccount(UUID id, boolean block) {
        return updateAccountStatus(id, block ? AccountStatus.BLOKE : AccountStatus.OPEN);
    }

    @Transactional
    public Account closeAccount(UUID id) {
        return updateAccountStatus(id, AccountStatus.CLOSE);
    }

    public Account updateAccountStatus(UUID accountId, AccountStatus status) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> getAccountNotFound(accountId));
        accountValidator.checkCloseAccount(account);

        if (Objects.equals(account.getStatus(), status)) {
            log.info("Account {} already has status {}", account.getNumber(), status);
            return account;
        }

        account.setStatus(status);
        log.info("Account has been update {}", account);

        return account;
    }

    private AccountNotFoundException getAccountNotFound(UUID accountId) {
        log.error("Account with {} not found", accountId);
        return new AccountNotFoundException(accountId);
    }
}
