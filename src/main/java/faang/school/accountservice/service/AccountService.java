package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public Account getAccountById(Long id) {
        return accountRepository.findByIdOrThrow(id);
    }

    @Transactional(readOnly = true)
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumberOrThrow(accountNumber);
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByOwner(Long ownerId, OwnerType ownerType) {
        return accountRepository.findByOwnerIdAndOwnerType(ownerId, ownerType);
    }

    @Transactional
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class, backoff = @Backoff(delay = 100))
    public Account openAccount(Account account) {
        // В строку ниже необходимо внедрить метод репозитория который ищет номер платежного счета.
        // Сейчас я просто поставил генератор рандомных чисел
        String accountNumber = String.valueOf((long)(Math.random() * 100000000000000000L) + 100000000000L);
        account.setAccountNumber(accountNumber);
        return accountRepository.save(account);
    }

    @Transactional
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class, backoff = @Backoff(delay = 100))
    public Account blockAccount(Long id) {
        Account account = accountRepository.findByIdOrThrow(id);
        if (account.getAccountStatus() == AccountStatus.BLOCKED) {
            log.info("Account {} is already blocked", id);
            return account;
        }
        account.setAccountStatus(AccountStatus.BLOCKED);
        return accountRepository.save(account);
    }

    @Transactional
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class, backoff = @Backoff(delay = 100))
    public Account closeAccount(Long id) {
        Account account = accountRepository.findByIdOrThrow(id);
        if (account.getAccountStatus() == AccountStatus.CLOSED) {
            log.info("Account {} is already closed", id);
            return account;
        }
        account.setAccountStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

}
