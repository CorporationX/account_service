package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountOpenRequest;
import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountStatusException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public void open(AccountOpenRequest request) {
        String accountNumber = accountNumberGenerator.generateUniqueAccountNumber();
        Account account = new Account(request.ownerId(), request.ownerType(), request.type(),
                request.currency(), accountNumber);
        accountRepository.save(account);
        log.info("Account {} created for owner {} (type: {})", accountNumber, request.ownerId(), request.ownerType());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse get(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> get(Long ownerId, OwnerType ownerType) {
        List<Account> accounts = accountRepository.findByOwnerIdAndOwnerType(ownerId, ownerType);
        return accountMapper.toDtoList(accounts);
    }

    @Override
    @Transactional
    public void block(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        if (account.getStatus() == AccountStatus.FROZEN) {
            log.warn("Payment account {} is not ACTIVE (actual: {}). No action taken.",
                    accountNumber, account.getStatus());
            return;
        }
        if (account.getStatus() != AccountStatus.ACTIVE) {
            log.error("Payment account with number {} cannot be blocked because its status is not ACTIVE", accountNumber);
            throw new AccountStatusException("Only accounts with status ACTIVE can be blocked");
        }
        account.setStatus(AccountStatus.FROZEN);
        log.info("Account {} status changed to FROZEN. Version: {}", accountNumber, account.getVersion());
    }

    @Override
    @Transactional
    public void close(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        if (account.getStatus() == AccountStatus.CLOSED) {
            log.warn("Payment account {} is already CLOSE. No action taken.", accountNumber);
            return;
        }
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(Instant.now());
        log.info("Payment account {} has been closed", accountNumber);
    }

    @Override
    @Transactional
    public void updateBalance(String accountNumber, BigDecimal amount) {
        Account account = getAccountOrThrow(accountNumber);
        if (account.getStatus() == AccountStatus.CLOSED) {
            log.error("Cannot update balance for account {}: status is CLOSED", accountNumber);
            throw new AccountStatusException("Balance update is forbidden: account is closed.");
        }
        BigDecimal newBalance = account.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            log.error("Insufficient funds: attempted to subtract {}, current balance: {}",
                    amount, account.getBalance());
            throw new IllegalStateException("Insufficient funds");
        }
        account.setBalance(newBalance);
        log.info("Account {} balance updated to {}. Version: {}", accountNumber, newBalance, account.getVersion());
    }

    private Account getAccountOrThrow(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.warn("Account with number {} not found", accountNumber);
                    return new AccountNotFoundException("account with number %s not found".formatted(accountNumber));
                });
    }
}
