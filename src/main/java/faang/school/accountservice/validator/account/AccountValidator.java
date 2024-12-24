package faang.school.accountservice.validator.account;

import faang.school.accountservice.exception.account.AccountNotFoundException;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountValidator {
    private final AccountRepository accountRepository;

    public Account validateAccountExists(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            log.error("Account with id {} not found", id);
            throw new AccountNotFoundException("Tariff with id: " + id + " does not exist");
        }
        return account.get();
    }
}