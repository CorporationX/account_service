package faang.school.accountservice.validator.savings_account;

import faang.school.accountservice.exception.savings_account.SavingsAccountNotFoundException;
import faang.school.accountservice.model.savings_account.SavingsAccount;
import faang.school.accountservice.repository.SavingsAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SavingsAccountValidator {
    private final SavingsAccountRepository savingsAccountRepository;

    public SavingsAccount validateSavingsAccountExists(Long accountId) {
        Optional<SavingsAccount> savingsAccount = savingsAccountRepository.findById(accountId);
        if (savingsAccount.isEmpty()) {
            log.error("Savings account with id{} not found", accountId);
            throw new SavingsAccountNotFoundException("Savings account with id: " + accountId + " does not exist");
        }
        return savingsAccount.get();
    }
}