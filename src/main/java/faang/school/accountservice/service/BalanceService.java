package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.BalanceNotFoundException;
import faang.school.accountservice.exception.OperationNotAllowed;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Balance createBalance(UUID accountId) {

        log.info("Creating balance for account {}", accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        Balance balance = Balance.builder()
                .account(account)
                .actualBalance(BigDecimal.ZERO)
                .authorizedBalance(BigDecimal.ZERO)
                .build();

        Balance saved = balanceRepository.save(balance);

        log.info("Balance created for account {} with id {}", accountId, saved.getId());

        return saved;
    }

    public Balance getBalance(UUID accountId) {

        log.debug("Fetching balance for account {}", accountId);

        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BalanceNotFoundException(accountId));
    }

    @Transactional
    public Balance authorize(UUID accountId, BigDecimal amount) {

        log.info("Authorizing {} for account {}", amount, accountId);

        Balance balance = balanceRepository.findByAccountIdForUpdate(accountId)
                .orElseThrow(() -> new BalanceNotFoundException(accountId));

        validateEnoughActual(balance, amount);

        balance.setActualBalance(balance.getActualBalance().subtract(amount));
        balance.setAuthorizedBalance(balance.getAuthorizedBalance().add(amount));

        log.info("Authorization completed for account {}. Authorized: {}, Actual: {}",
                accountId, balance.getAuthorizedBalance(), balance.getActualBalance());

        return balance;
    }

    @Transactional
    public Balance clearing(UUID accountId, BigDecimal amount) {

        log.info("Clearing {} for account {}", amount, accountId);

        Balance balance = balanceRepository.findByAccountIdForUpdate(accountId)
                .orElseThrow(() -> new BalanceNotFoundException(accountId));

        validateEnoughAuthorized(balance, amount);

        balance.setAuthorizedBalance(balance.getAuthorizedBalance().subtract(amount));

        log.info("Clearing finished for account {}. New authorized balance: {}",
                accountId, balance.getAuthorizedBalance());

        return balance;
    }

    @Transactional
    public Balance voidAuthorization(UUID accountId, BigDecimal amount) {

        log.info("Voiding authorization {} for account {}", amount, accountId);

        Balance balance = balanceRepository.findByAccountIdForUpdate(accountId)
                .orElseThrow(() -> new BalanceNotFoundException(accountId));

        validateEnoughAuthorized(balance, amount);

        balance.setAuthorizedBalance(balance.getAuthorizedBalance().subtract(amount));
        balance.setActualBalance(balance.getActualBalance().add(amount));

        log.info("Authorization voided for account {}. Authorized: {}, Actual: {}",
                accountId, balance.getAuthorizedBalance(), balance.getActualBalance());

        return balance;
    }

    public void validateEnoughActual(Balance balance, BigDecimal amount) {
        if (balance.getActualBalance().compareTo(amount) < 0) {
            throw new OperationNotAllowed("Not enough funds for authorization");
        }
    }

    public void validateEnoughAuthorized(Balance balance, BigDecimal amount) {
        if (balance.getAuthorizedBalance().compareTo(amount) < 0) {
            throw new OperationNotAllowed("Not enough authorized funds");
        }
    }
}
