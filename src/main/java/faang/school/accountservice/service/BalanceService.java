package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.exception.BalanceException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private static final String BALANCE_NOT_FOUND = "Balance for account with id = %s not found";
    private static final String BALANCE_CREATED = "Balance for account with id = %s is created";
    private static final String ACCOUNT_NOT_FOUND = "Account with id = %s not found";
    private static final String NOT_ENOUGH_FUNDS = "There are not enough funds in the account balance id = %s (amount = %s)";
    private static final String NOT_ENOUGH_FUNDS_ON_AUTHORIZATION_BALANCE = "There are not enough funds in the account on authorization balance id = %s (amount = %s)";
    private static final String CLEARING_BALANCE_SUCCESSFULLY = "Account (id = %s) balance clearing completed successfully";
    private static final String AUTHORIZATION_BALANCE_UPDATED = "Authorization balance for %s account is updated (amount = %s)";

    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;
    private final BalanceMapper balanceMapper;

    public BalanceDto getBalance(Long accountId) {
        Balance balance = getBalanceCurrentBalance(accountId);
        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceDto createBalance(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
                new EntityNotFoundException(String.format(ACCOUNT_NOT_FOUND, accountId))
        );

        Balance balance = createDefaultBalance(account);
        account.setBalance(balance);

        balance = balanceRepository.save(balance);

        log.info(String.format(BALANCE_CREATED, account.getAccountNumber()));

        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceDto authorizationBalance(Long accountId, BigDecimal amount) {
        Balance balance = getBalanceCurrentBalance(accountId);

        BigDecimal actualBalance = balance.getActualBalance();
        BigDecimal authorizationBalance = balance.getAuthorizationBalance();

        if (authorizationBalance.compareTo(BigDecimal.ZERO) > 0) {
            actualBalance = actualBalance.add(authorizationBalance);
            authorizationBalance = BigDecimal.valueOf(0.0);
        }

        validateAuthorizationBalance(actualBalance, amount, accountId);

        Balance balanceSaved = saveBalance(balance, actualBalance.subtract(amount), authorizationBalance.add(amount));

        log.info(String.format(AUTHORIZATION_BALANCE_UPDATED, accountId, authorizationBalance));
        return balanceMapper.toDto(balanceSaved);
    }

    @Transactional
    public BalanceDto updateActualBalance(Long accountId, BigDecimal amount) {
        Balance balance = getBalanceCurrentBalance(accountId);

        BigDecimal actualBalance = balance.getActualBalance().add(amount);
        BigDecimal authorizationBalance = balance.getAuthorizationBalance();

        Balance balanceSaved = saveBalance(balance, actualBalance, authorizationBalance);

        log.info(String.format(AUTHORIZATION_BALANCE_UPDATED, accountId, authorizationBalance));
        return balanceMapper.toDto(balanceSaved);
    }

    @Transactional
    public BalanceDto updateAuthorizationBalance(Long accountId, BigDecimal amount) {
        Balance balance = getBalanceCurrentBalance(accountId);

        BigDecimal actualBalance = balance.getActualBalance();
        BigDecimal authorizationBalance = balance.getAuthorizationBalance();

        validateUpdatingAuthorizationBalance(authorizationBalance, amount, accountId);

        Balance balanceSaved = saveBalance(balance, actualBalance, authorizationBalance.subtract(amount));

        log.info(String.format(AUTHORIZATION_BALANCE_UPDATED, accountId, authorizationBalance));
        return balanceMapper.toDto(balanceSaved);
    }

    @Transactional
    public BalanceDto clearingBalance(Long accountId) {
        Balance balance = getBalanceCurrentBalance(accountId);

        BigDecimal authorizationBalance = balance.getAuthorizationBalance();
        BigDecimal actualBalance = balance.getActualBalance().add(authorizationBalance);
        authorizationBalance = BigDecimal.valueOf(0.0);

        Balance balanceSaved = saveBalance(balance, actualBalance, authorizationBalance);
        log.info(String.format(CLEARING_BALANCE_SUCCESSFULLY, accountId));
        return balanceMapper.toDto(balanceSaved);
    }

    private Balance getBalanceCurrentBalance(Long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format(BALANCE_NOT_FOUND, accountId))
                );
    }

    private void validateAuthorizationBalance(BigDecimal actualBalance, BigDecimal amount, long accountId) {
        if (actualBalance.compareTo(BigDecimal.ZERO) <= 0 &&
                amount.compareTo(BigDecimal.ZERO) > 0) {
            throw new BalanceException(String.format(NOT_ENOUGH_FUNDS, accountId, amount));
        }
    }

    private void validateUpdatingAuthorizationBalance(BigDecimal authorizationBalance, BigDecimal amount, long accountId) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 && authorizationBalance.compareTo(amount) < 0) {
            throw new BalanceException(String.format(NOT_ENOUGH_FUNDS_ON_AUTHORIZATION_BALANCE, accountId, amount));
        }
    }

    private Balance saveBalance(Balance balance, BigDecimal actualBalance, BigDecimal authorizationBalance) {

        balance.setActualBalance(actualBalance);
        balance.setAuthorizationBalance(authorizationBalance);
        balance.setUpdatedAt(LocalDateTime.now());
        return balanceRepository.save(balance);
    }

    private Balance createDefaultBalance(Account account) {
        return Balance.builder()
                .account(account)
                .actualBalance(BigDecimal.ZERO)
                .authorizationBalance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

