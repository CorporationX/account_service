package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.AccountOwnerType;
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

import java.time.LocalDateTime;
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
    public Account createAccount(Account account, UUID currencyId, AccountOwnerType ownerType) {
        String number = accountNumberGenerator.generateAccountNumber();
        account.setNumber(number);

        Currency currency = currencyService.getCurrencyById(currencyId);
        account.setCurrency(currency);
        account.setStatus(AccountStatus.OPEN);
        account.setOwnerType(ownerType);


        Account savedAccount = accountRepository.save(account);
        log.info("Account {} has been saved", savedAccount);

        return savedAccount;
    }

    @Transactional
    public Account blockAccount(UUID accountId, boolean block) {
        Account account = updateAccountStatus(accountId, block ? AccountStatus.BLOCKED : AccountStatus.OPEN);
        log.info("Account has been updated {}", account);
        return account;
    }

    @Transactional
    public Account closeAccount(UUID accountId) {
        Account account = updateAccountStatus(accountId, AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        log.info("Account has been updated {}", account);
        return account;
    }

    private Account updateAccountStatus(UUID accountId, AccountStatus status) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> getAccountNotFound(accountId));
        accountValidator.checkCloseAccount(account);

        if (Objects.equals(account.getStatus(), status)) {
            log.info("Account {} already has status {}", account.getNumber(), status);
            return account;
        }

        account.setStatus(status);

        return account;
    }

    private AccountNotFoundException getAccountNotFound(UUID accountId) {
        log.error("Account with id {} not found", accountId);
        return new AccountNotFoundException(accountId);
    }
}
