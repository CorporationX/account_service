package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.AccountDtoToUpdate;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Owner;
import faang.school.accountservice.model.enums.AccountStatus;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.OwnerRepository;
import faang.school.accountservice.service.balance.BalanceService;
import faang.school.accountservice.validator.AccountValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final BalanceService balanceService;
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private final AccountValidator accountValidator;
    private final OwnerRepository ownerRepository;

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    public AccountDto open(AccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);

        accountValidator.validateCreate(account);

        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setVersion(1);
        setAccountOwner(account);

        balanceService.createBalance(account);
        accountRepository.save(account);
        log.info("Created new account: {}", account.getId());
        return accountMapper.toDto(account);
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    public AccountDto update(long accountId, AccountDtoToUpdate accountDto) {
        Account account = findAccountById(accountId);

        accountMapper.update(accountDto, account);
        account.setVersion(account.getVersion() + 1);

        accountRepository.save(account);
        log.info("Updated account: {}", accountId);
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

        accountValidator.validateBlock(account);

        account.setAccountStatus(AccountStatus.FROZEN);
        account.setVersion(account.getVersion() + 1);

        accountRepository.save(account);
        log.info("Blocked account: {}", accountId);
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    public void unBlock(long accountId) {
        Account account = findAccountById(accountId);

        accountValidator.validateUnblock(account);

        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setVersion(account.getVersion() + 1);

        accountRepository.save(account);
        log.info("Unblocked account: {}", accountId);
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    public void close(long accountId) {
        Account account = findAccountById(accountId);

        accountValidator.validateClose(account);

        account.setAccountStatus(AccountStatus.CLOSED);
        account.setVersion(account.getVersion() + 1);
        account.setClosedAt(Instant.now());

        accountRepository.save(account);
        log.info("Closed account: {}", accountId);
    }

    @Transactional(readOnly = true)
    public Account findAccountById(long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(String.format("Account with id: %d not found", accountId)));
    }

    private void setAccountOwner(Account account) {
        Owner owner = account.getOwner();
        Optional<Owner> existingOwner = ownerRepository.findByAccountIdAndOwnerType(
                owner.getAccountId(), owner.getOwnerType());

        if (existingOwner.isEmpty()) {
            Owner newOwner = new Owner();
            newOwner.setAccountId(owner.getAccountId());
            newOwner.setOwnerType(owner.getOwnerType());
            newOwner = ownerRepository.save(newOwner);
            account.setOwner(newOwner);
        } else {
            account.setOwner(existingOwner.get());
        }
    }
}