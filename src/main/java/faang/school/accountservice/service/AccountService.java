package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Owner;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.OwnerRepository;
import faang.school.accountservice.validation.AccountValidate;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final BalanceService balanceService;
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private final AccountValidate accountValidate;
    private final OwnerRepository ownerRepository;

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    public AccountDto open(AccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);
        //accountValidate.validate(account);
        account.setAccountStatus(AccountStatus.ACTIVE);
        setUpOwner(account);
        accountRepository.save(account);
        balanceService.createBalance(account);
        return accountMapper.toDto(account);
    }

    @Transactional(readOnly = true)
    public AccountDto get(long accountId) {
        return accountMapper.toDto(findAccountById(accountId));
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    public void block(long accountId) {
        Account account = findAccountById(accountId);
        account.setAccountStatus(AccountStatus.FROZEN);
        accountRepository.save(account);
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    public void unBlock(long accountId) {
        Account account = findAccountById(accountId);
        account.setAccountStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    public void delete(long accountId) {
        Account account = findAccountById(accountId);
        account.setAccountStatus(AccountStatus.CLOSED);
        account.setClosedAt(Instant.now());
        accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Account findAccountById(long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Account with id: %d not found", accountId)));
    }

    private void setUpOwner(Account account) {
        Owner owner = account.getOwner();
        Optional<Owner> optionalOwner = ownerRepository.findByAccountIdAndOwnerType(owner.getAccountId(), owner.getOwnerType());
        optionalOwner.ifPresentOrElse(account::setOwner, () -> {});
    }
}
