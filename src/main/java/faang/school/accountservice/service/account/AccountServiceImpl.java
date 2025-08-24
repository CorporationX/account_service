package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.DataConflictRetryException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.account.AccountValidator;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountValidator accountValidator;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountDto create(@NonNull CreateAccountDto createAccountDto) {
        accountValidator.validateCreate(createAccountDto);
        Account account = accountMapper.toAccount(createAccountDto);
        AccountStatus status = createAccountDto.accountStatus() != null
                ? createAccountDto.accountStatus()
                : AccountStatus.ACTIVE;
        account.setStatus(status);
        return accountMapper.toAccountDto(accountRepository.save(account));
    }

    @Override
    public AccountDto getAccountById(@NonNull Long id) {
        return accountMapper.toAccountDto(getAccountByIdOrFail(id));
    }

    @Transactional
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2)
    )
    @Override
    public AccountDto updateAccountStatus(@NonNull Long id, @NonNull AccountStatus status) {
        Account account = getAccountByIdOrFail(id);
        accountValidator.validateStatusTransition(account, status);
        account.setStatus(status);
        if (status == AccountStatus.CLOSED) {
            account.setClosedAt(LocalDateTime.now());
        }
        return accountMapper.toAccountDto(accountRepository.saveAndFlush(account));
    }

    private Account getAccountByIdOrFail(@NonNull Long id) {
        return accountRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Account with ID " + id + " not found"));
    }

    @Recover
    AccountDto recoverFromOptimisticFailure(ObjectOptimisticLockingFailureException exception,
                                            Long id, AccountStatus status) {
        throw new DataConflictRetryException(
                "Failed to update account " + id + " status to "
                        + status + " due to conflicting operations. Please try again"
        );
    }
}
