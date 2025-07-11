package faang.school.accountservice.account.strategy;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class SavingsAccountAction implements AccountTypeSpecifiedAction {
    private final SavingsAccountRepository savingsAccountRepository;
    private final AccountRepository accountRepository;

    @Override
    public Account createAccount(Account account) {
        SavingsAccount savingsAccount = new SavingsAccount();

        savingsAccount.setId(account.getId());
        savingsAccount.setNumber(account.getNumber());
        savingsAccount.setOwnerType(account.getOwnerType());
        savingsAccount.setOwnerId(account.getOwnerId());
        savingsAccount.setType(account.getType());
        savingsAccount.setCurrency(account.getCurrency());
        savingsAccount.setStatus(account.getStatus());
        savingsAccount.setCreatedAt(account.getCreatedAt());
        savingsAccount.setUpdatedAt(account.getUpdatedAt());
        savingsAccount.setClosedAt(account.getClosedAt());
        savingsAccount.setVersion(account.getVersion());

        savingsAccount.setTariffStory(new ArrayList<>());
        savingsAccount.setLastInterestAccrualAt(null);

        SavingsAccount savedAccount = savingsAccountRepository.save(savingsAccount);
        return savedAccount;
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.SAVINGS;
    }
}
