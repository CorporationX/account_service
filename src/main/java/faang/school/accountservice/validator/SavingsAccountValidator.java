package faang.school.accountservice.validator;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.SavingsAccount;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.repository.account.SavingsAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SavingsAccountValidator {

    private final AccountRepository accountRepository;
    private final SavingsAccountRepository savingsAccountRepository;

    public void validateAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("The account with id " +
                        accountId + " does not exist!"));

        if (!account.getAccountType().equals(AccountType.SAVINGS)) {
            throw new RuntimeException("The account with id" + accountId + "is not a savings account!");
        }
    }

    public void validateTariffForUpdates(String tariff, String lastTariff) {
        if(tariff.equals(lastTariff)) {
            throw new RuntimeException("The rates cannot be equal!");
        }
    }

    public void validateAccountById(UUID id) {
        savingsAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The saving account with id " +
                        id + " does not exist!"));
    }
}
