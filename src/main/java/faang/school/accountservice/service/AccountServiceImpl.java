package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AccountStatus;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountDto getAccount(String accountNumber) {
        Account account = validateAccountNumber(accountNumber);
        log.info("Account with number {} found", accountNumber);

        return accountMapper.mapToDto(account);
    }

    @Override
    public AccountDto openAccount(AccountDto accountDto) {
        Account account = accountMapper.mapToEntity(accountDto);
        account.setStatus(AccountStatus.ACTIVE);
        account = accountRepository.save(account);
        log.info("Account with id {} created", account.getId());
        return accountMapper.mapToDto(account);
    }

    @Override
    @Transactional
    public AccountDto blockAccount(String accountNumber) {
        Account account = validateAccountNumber(accountNumber);
        account.setStatus(AccountStatus.FROZEN);
        incrementAccountVersion(account);
        return accountMapper.mapToDto(accountRepository.save(account));
    }

    @Override
    @Transactional
    public AccountDto unblockAccount(String accountNumber) {
        Account account = validateAccountNumber(accountNumber);
        account.setStatus(AccountStatus.ACTIVE);
        incrementAccountVersion(account);
        return accountMapper.mapToDto(accountRepository.save(account));
    }

    @Override
    @Transactional
    public AccountDto closeAccount(String accountNumber) {
        Account account = validateAccountNumber(accountNumber);
        account.setStatus(AccountStatus.CLOSED);
        incrementAccountVersion(account);
        return accountMapper.mapToDto(accountRepository.save(account));
    }

    private Account validateAccountNumber(String accountNumber) {
        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);
        if (optionalAccount.isEmpty()) {
            log.error("Account with number {} not found", accountNumber);
            throw new EntityNotFoundException("Account not found");
        }
        return optionalAccount.get();
    }

    @Transactional
    private void incrementAccountVersion(Account account) {
        long version = account.getAccountVersion();
        account.setAccountVersion(++version);
        accountRepository.save(account);
        log.info("Account version incremented to {}", version);
    }
}