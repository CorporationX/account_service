package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Slf4j
@Service
@AllArgsConstructor
public class AccountService {

    private final AccountOwnerService accountOwnerService;
    private final FreeAccountNumbersService freeAccountNumbersService;
    private final BalanceService balanceService;
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public AccountDto getAccount(AccountDto accountDto) {
        Account account = getAccountForUpdate(accountDto.getId());
        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto openAccount(AccountDto accountDto) {
        AccountOwner accountOwner = accountOwnerService.getAndCreateIfNecessary(accountDto);
        BigInteger accountNumber = freeAccountNumbersService.getFreeNumberAndDeleteFromCache(accountDto.getAccountType());
        Balance balance = balanceService.createBalance();
        Account account = new Account(accountNumber, balance, accountOwner, AccountStatus.ACTIVE, accountDto);
        return accountMapper.toDto(accountRepository.save(account));
    }

    @Transactional
    @Retryable(backoff = @Backoff(delay = 300))
    public AccountDto suspendAccount(AccountDto accountDto) {
        Account account = getAccountForUpdate(accountDto.getId());
        if (!account.getAccountStatus().equals(AccountStatus.SUSPENDED) &&
                !account.getAccountStatus().equals(AccountStatus.CLOSED)) {
            account.setAccountStatus(AccountStatus.SUSPENDED);
            return accountMapper.toDto(accountRepository.save(account));
        } else {
            log.info("Account {} status is already SUSPENDED or CLOSED", account.getAccountNumber());
            throw new RuntimeException("Account status is already SUSPENDED or CLOSED");
        }
    }

    @Transactional
    @Retryable(backoff = @Backoff(delay = 300))
    public AccountDto closeAccount(AccountDto accountDto) {
        Account account = getAccountForUpdate(accountDto.getId());
        if (!account.getAccountStatus().equals(AccountStatus.CLOSED)) {
            account.setAccountStatus(AccountStatus.CLOSED);
            return accountMapper.toDto(accountRepository.save(account));
        } else {
            log.info("Account {} status is already CLOSED", account.getAccountNumber());
            throw new RuntimeException("Account status is already CLOSED");
        }
    }

    private Account getAccountForUpdate(long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> {
            log.info("Account with id {} not found", accountId);
            return new EntityNotFoundException("Account with id " + accountId + " not found");
        });
    }
}
