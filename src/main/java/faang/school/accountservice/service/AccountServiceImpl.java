package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.util.AccountNumberGenerator;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountNumberGenerator accountNumberGenerator;

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return accountMapper.toDto(account);
    }

    @Override
    public AccountDto openAccount(AccountDto accountDto) {
        log.info("Opening new account for ownerId={}, type={}", accountDto.getOwnerId(), accountDto.getAccountType());

        Account account = accountMapper.toEntity(accountDto);
        account.setAccountNumber(accountNumberGenerator.generateAccountNumber());
        account.setStatus(AccountStatus.ACTIVE);

        Account savedAccount = accountRepository.save(account);
        log.info("Account successfully created. Account  number={}", savedAccount.getAccountNumber());

        return accountMapper.toDto(savedAccount);
    }

    @Override
    public AccountDto blockAccount(Long id) {
        log.info("Blocking account with id={}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalStateException("Cannot block a closed account.");
        }

        try {
            account.setStatus(AccountStatus.FROZEN);
            Account savedAccount = accountRepository.save(account);
            log.info("Account with id={} has been blocked", savedAccount.getId());

            return accountMapper.toDto(savedAccount);
        } catch (JpaOptimisticLockingFailureException e) {
            log.error("Optimistic lock exception occurred while blocking account with id={}", id);
            throw e;
        }
    }

    @Override
    public AccountDto closeAccount(Long id) {
        log.info("Closing account with id={}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalStateException("Account is already closed.");
        }

        try {
            account.setStatus(AccountStatus.CLOSED);
            account.setClosedAt(LocalDateTime.now());

            Account savedAccount = accountRepository.save(account);
            log.info("Account with id={} has been closed", savedAccount.getId());

            return accountMapper.toDto(savedAccount);
        } catch (JpaOptimisticLockingFailureException e) {
            log.error("Optimistic lock exception occurred while closing account with id={}", id);
            throw e;
        }
    }
}




