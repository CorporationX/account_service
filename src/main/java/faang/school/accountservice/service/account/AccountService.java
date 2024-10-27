package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.account.AccountStatus;
import faang.school.accountservice.exception.account.AccountHasBeenUpdateException;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.balance.BalanceService;
import faang.school.accountservice.validator.AccountValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountValidator validator;
    private final BalanceService balanceService;

    @Transactional
    public Account openAccount(Account account) {
        validator.validateOpenAccount(account);

        Account finalAccount = account;

        String accountNumber = "408124878517";
        account.setStatus(AccountStatus.ACTIVE);
        account.setNumber(accountNumber);
        account.setCreatedAt(LocalDateTime.now());

        saveAccount(account);
        balanceService.createBalance(account);

        return account;
    }

    @Transactional(readOnly = true)
    public Account getAccountById(UUID id) {
        return findAccountById(id);
    }

    @Transactional
    public Account closeAccount(UUID id) {
        Account account = findAccountById(id);
        validator.validateNotActiveAccount(account);
        LocalDateTime currentDateTime = LocalDateTime.now();

        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(currentDateTime);
        account.setUpdatedAt(currentDateTime);

        saveAccount(account);

        return account;
    }

    @Transactional
    public Account blockAccount(UUID id) {
        Account account = findAccountById(id);
        validator.validateNotActiveAccount(account);

        account.setStatus(AccountStatus.BLOCKED);
        account.setUpdatedAt(LocalDateTime.now());

        saveAccount(account);

        return account;
    }

    private Account findAccountById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Account.class, id));
    }

    private Account saveAccount(Account account) {
        try {
            account = accountRepository.save(account);
            accountRepository.flush();
        } catch (OptimisticLockingFailureException exception) {
            throw new AccountHasBeenUpdateException(account.getId());
        }

        return account;
    }
}
