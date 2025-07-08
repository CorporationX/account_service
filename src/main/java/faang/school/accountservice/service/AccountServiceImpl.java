package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountStateException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.utils.AccountNumberGeneratorUtil;
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
    public AccountDto openAccount(AccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);
        account.setStatus(AccountStatus.ACTIVE);
        account.setNumber(AccountNumberGeneratorUtil.generateAccountNumber());

        return accountMapper.toDto(accountRepository.save(account));
    }

    @Override
    public AccountDto getAccount(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional
    public void closeAccount(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountStateException(
                    String.format("Account with AccountNumber:%s is already closed", accountNumber));
        }
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());

        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void blockAccount(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountStateException(
                    String.format("Account with AccountNumber:%s is already closed", accountNumber));
        }
        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new AccountStateException(
                    String.format("Account with AccountNumber:%s is already blocked", accountNumber));
        }
        account.setStatus(AccountStatus.BLOCKED);

        accountRepository.save(account);
    }

    public Account getAccountOrThrow(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("Account with accountNumber: {} not found.", accountNumber);
                    return new AccountNotFoundException(
                            String.format("Account with accountNumber: %s not found.", accountNumber));
                });
    }
}
