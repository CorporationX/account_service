package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.currency.Currency;
import faang.school.accountservice.exception.account.AccountNotFoundException;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final CurrencyService currencyService;

    @Transactional(readOnly = true)
    public Account getAccountById(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account with {} not found", accountId);
                    return new AccountNotFoundException(accountId);
                });
    }

    @Transactional
    public Account createAccount(Account account, UUID currencyId) {
        Currency currency = currencyService.getCurrencyById(currencyId);
        account.setCurrency(currency);

        // TODO: сиквенс для номера

        Account savedAccount = accountRepository.save(account);
        log.info("Account {} has been saved", savedAccount);

        return savedAccount;
    }
}
