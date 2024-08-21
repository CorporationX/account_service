package faang.school.accountservice.validator.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AccountValidator {
    private final AccountRepository accountRepository;

    public AccountStatus checkForMatchStatusAndTransformation(String status) {
        AccountStatus accountStatus;
        try {
            accountStatus = AccountStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            log.error("Некорректный статус аккаунта: {}", status);
            throw new IllegalArgumentException("Некорректный статус аккаунта: " + status);
        }
        return accountStatus;
    }

    public void checkExistenceOfTheNumber(Account account) {
        if (accountRepository.existsByNumber(account.getNumber())) {
            log.error("Счет с номером {} уже существует", account.getNumber());
            throw new IllegalArgumentException("Счет с номером {} уже существует");
        }
    }
}