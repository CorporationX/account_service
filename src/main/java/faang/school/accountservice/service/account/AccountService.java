package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.account.Status;
import faang.school.accountservice.exception.account.AccountHasBeenUpdateException;
import faang.school.accountservice.exception.account.AccountNotFoundException;
import faang.school.accountservice.exception.account.GenerateAccountNumberException;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.AccountValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountValidator validator;
    private final List<GeneratorAccountNumber> generatorAccountNumbers;

    @Transactional
    public Account openAccount(Account account) {
        validator.validateOpenAccount(account);

        Account finalAccount = account;

        String accountNumber = generatorAccountNumbers.stream()
                .filter(item -> finalAccount.getType().equals(item.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new GenerateAccountNumberException(finalAccount.getType()))
                .generateNumber();

        account.setStatus(Status.ACTIVE);
        account.setNumber(accountNumber);
        account.setCreatedAt(LocalDateTime.now());

        account = accountRepository.save(account);

        return account;
    }

    @Transactional(readOnly = true)
    public Account getAccountById(Long id) {
        return findAccountById(id);
    }

    @Transactional
    public Account closeAccount(Long id) {
        Account account = findAccountById(id);
        validator.validateCloseAccount(account);
        LocalDateTime currentDateTime = LocalDateTime.now();

        account.setStatus(Status.CLOSED);
        account.setClosedAt(currentDateTime);
        account.setUpdatedAt(currentDateTime);

        try {
            account = accountRepository.save(account);
            accountRepository.flush();
        } catch (OptimisticLockingFailureException exception) {
            throw new AccountHasBeenUpdateException(id);
        }

        return account;
    }

    private Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }
}
