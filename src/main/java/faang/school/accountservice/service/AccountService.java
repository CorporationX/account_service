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
    public AccountDto get(AccountDto accountDto) {
        Account account = getAccountForUpdate(accountDto.getId());
        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto open(AccountDto accountDto) {
        AccountOwner accountOwner = accountOwnerService.getAndCreateIfNecessary(accountDto);
        BigInteger accountNumber = freeAccountNumbersService.getFreeNumberAndDeleteFromCache(accountDto.getAccountType());
        Balance balance = balanceService.createBalance();
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .balance(balance)
                .accountOwner(accountOwner)
                .accountType(accountDto.getAccountType())
                .currency(accountDto.getCurrency())
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        return accountMapper.toDto(accountRepository.save(account));
    }

    @Transactional
    @Retryable(backoff = @Backoff(delay = 300))
    public AccountDto block(AccountDto accountDto) {
        Account account = getAccountForUpdate(accountDto.getId());
        account.setAccountStatus(AccountStatus.FROZEN);
        return accountMapper.toDto(accountRepository.save(account));
    }

    @Transactional
    @Retryable(backoff = @Backoff(delay = 300))
    public AccountDto close(AccountDto accountDto) {
        Account account = getAccountForUpdate(accountDto.getId());
        account.setAccountStatus(AccountStatus.CLOSED);
        return accountMapper.toDto(accountRepository.save(account));
    }

    private Account getAccountForUpdate(long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> {
            log.info("Account with id {} not found", accountId);
            return new EntityNotFoundException("Account with id " + accountId + " not found");
        });
    }
}
