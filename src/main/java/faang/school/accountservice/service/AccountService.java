package faang.school.accountservice.service;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;

    public Account getCurrencyAccountByOwner(long ownerId, Currency currency) {
        Optional<Account> accountOpt = accountRepository.findCurrencyAccountByOwner(ownerId, currency.ordinal());
        if(accountOpt.isEmpty()) {
            String message = "user with id = %d does not have account with currency %s".formatted(ownerId, currency);
            log.error(message);
            throw new RuntimeException(message);
        }

        return accountOpt.get();
    }

    public Account getAccount(long id) {
        Optional<Account> accountOpt = accountRepository.findById(id);
        if (accountOpt.isEmpty()) {
            String message = "Account with id = %d does not exist".formatted(id);
            log.error(message);
            throw new RuntimeException(message);
        }
        return accountOpt.get();
    }
}
