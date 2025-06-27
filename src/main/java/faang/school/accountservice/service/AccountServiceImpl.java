package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountStateException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.utils.PasswordGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;

    @Override
    public AccountDto open(AccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);
        account.setStatus(AccountStatus.ACTIVE);
        account.setNumber(PasswordGeneratorUtil.generatePassword());

        return accountMapper.toDto(accountRepository.save(account));
    }

    @Override
    public AccountDto get(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional
    public void close(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountStateException("Account already closed");
        }
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void block(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountStateException("Closed account cannot be blocked");
        }
        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new AccountStateException("Account already blocked");
        }
        account.setStatus(AccountStatus.FROZEN);
    }

    private Account getAccountOrThrow(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("Account not found: {}", accountNumber);
                    return new AccountNotFoundException(
                            "Account not found: " + accountNumber);
                });
    }
}
