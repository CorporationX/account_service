package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.exception.AccountInactiveException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final BalanceService balanceService;

    public AccountDto get(long id) {
        return accountMapper.toDto(getAccount(id));
    }

    @Retryable(retryFor = OptimisticLockException.class)
    public AccountDto open(AccountDto accountDto) {
        accountDto.setStatus(Status.ACTIVE);
        Currency accountDtoCurrency = accountDto.getCurrency();
        BalanceDto createdBalance = balanceService.createBalance(accountDtoCurrency);
        accountDto.setBalance(createdBalance);
        Account newAccount = accountRepository.save(accountMapper.toEntity(accountDto));
        return accountMapper.toDto(newAccount);
    }

    @Retryable(retryFor = OptimisticLockException.class)
    @Transactional
    public AccountDto close(long id) {
        Account account = getAccount(id);
        account.setStatus(Status.CLOSED);
        account.setClosedAt(Instant.now());
        return accountMapper.toDto(account);
    }

    @Retryable(retryFor = OptimisticLockException.class)
    @Transactional
    public AccountDto block(long id, Status status) {
        blockStatusValidate(status);
        Account account = getAccount(id);
        account.setStatus(status);
        return accountMapper.toDto(account);
    }

    public Account getAccount(long id) {
        return accountRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Account doesn't exist by id: %s", id)));
    }

    public void blockStatusValidate(Status status) {
        if (status != Status.BLOCKED && status != Status.SUSPENDED) {
            throw new IllegalArgumentException("You can use only BLOCKED or SUSPENDED status");
        }
    }

    public void accountAvailable(String number) {
        Account account = accountRepository.findByNumber(number)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        log.info("Not found account with number: {}", number);
        if (!account.getStatus().equals(Status.ACTIVE)) {
            log.info("Account is not active: {}", number);
            throw new AccountInactiveException("Account is not active");
        }
    }
}