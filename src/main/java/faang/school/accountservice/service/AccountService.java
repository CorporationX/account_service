package faang.school.accountservice.service;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final BalanceRepository balanceRepository;
    private final UserContext userContext;

    @Autowired
    public AccountService(AccountRepository accountRepository,
                          AccountMapper accountMapper,
                          BalanceRepository balanceRepository,
                          UserContext userContext) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.balanceRepository = balanceRepository;
        this.userContext = userContext;
    }

    public AccountDto getAccount(long id) {
        Account account = getAccountById(id);
        return accountMapper.toDto(account);
    }

    public AccountDto openAccount(AccountDto accountDto) {
        Optional<Account> account = accountRepository.findById(accountDto.id());
        if (account.isPresent()) {
            Account existingAccount = account.get();
            if (existingAccount.getStatus() == AccountStatus.ACTIVE) {
                throw new IllegalStateException("Account is already active");
            } else {
                existingAccount.setStatus(AccountStatus.ACTIVE);
                existingAccount.setClosedAt(null);
                Account updatedAccount = accountRepository.save(existingAccount);

                Balance balance = Balance.builder()
                        .account(updatedAccount)
                        .currentBalance(BigDecimal.ZERO)
                        .availableBalance(BigDecimal.ZERO)
                        .build();
                balanceRepository.save(balance);
                return accountMapper.toDto(updatedAccount);
            }
        }
        Account newAccount = accountMapper.toEntity(accountDto);
        newAccount.setOwnerId(userContext.getUserId());
        accountRepository.save(newAccount);
        return accountMapper.toDto(newAccount);
    }

    public AccountDto blockAccount(long id) {
        Account account = getAccountById(id);
        account.setStatus(AccountStatus.FROZEN);
        Account frozenAccount = accountRepository.save(account);
        return accountMapper.toDto(frozenAccount);
    }

    public AccountDto closeAccount(long id) {
        Account account = getAccountById(id);
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalStateException("Account is already closed");
        }
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        Account closedAccount = accountRepository.save(account);
        return accountMapper.toDto(closedAccount);
    }

    private Account getAccountById(long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            throw new AccountNotFoundException("Account not found");
        }
        return account.get();
    }
}
