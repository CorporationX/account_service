package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.InsufficientFundsException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ConcurrentModificationException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    @Transactional
    public BalanceDto createBalance(Currency currency) {
        Balance balance = new Balance();
        balance.setCurrency(currency);
        return balanceMapper.toDto(saveBalance(balance));
    }

    @Transactional
    public BalanceDto deposit(String accountNumber, BigDecimal amount) {
        Balance balance = getBalance(accountNumber);
        BigDecimal newAuthorizationBalance = balance.getAuthorizationBalance().add(amount);
        BigDecimal newActualBalance = balance.getActualBalance().add(amount);
        balance.setAuthorizationBalance(newAuthorizationBalance);
        balance.setActualBalance(newActualBalance);
        balance.versionIncrement();

        Balance saveBalance = saveBalance(balance);
        log.info("Deposit was successful an account: {}", accountNumber);
        return balanceMapper.toDto(saveBalance);
    }

    private Balance updateBalance(String accountNumber, Balance updateBalance) {
        Balance balance = getBalance(accountNumber);
        if (balance.getVersion() == updateBalance.getVersion()) {
            updateBalance.setUpdatedAt(Instant.now());
            updateBalance.versionIncrement();
            Balance savedBalance = saveBalance(updateBalance);
            log.info("Balance was successful updated an account:");
            return savedBalance;
        } else {
            throw new ConcurrentModificationException("Balance has been modified");
        }
    }

    @Transactional
    public void authorizePayment(String accountNumber, BigDecimal amount) {
        Balance balance = getBalance(accountNumber);
        BigDecimal authorizationBalance = balance.getAuthorizationBalance();
        if (balance.getAuthorizationBalance().compareTo(amount) >= 0) {
            BigDecimal newAuthorizationBalance = authorizationBalance.subtract(amount);
            balance.setAuthorizationBalance(newAuthorizationBalance);
            saveBalance(balance);
            log.info("Payment authorization was successful an account: {}", accountNumber);
        } else {
            log.info("Insufficient funds. Payment unsuccessful an account: {}", accountNumber);
            throw new InsufficientFundsException("Insufficient funds on account!");
        }
    }

    @Transactional
    public void clearingPayment(String accountNumber, BigDecimal amount) {
        Balance balance = getBalance(accountNumber);
        BigDecimal authorizationBalance = balance.getAuthorizationBalance();
        BigDecimal actualBalance = balance.getActualBalance();

        if (authorizationBalance.compareTo(actualBalance) <= 0) {
            BigDecimal newActualBalance = actualBalance.subtract(authorizationBalance);
            balance.setActualBalance(newActualBalance);
            Balance updatedBalance = updateBalance(accountNumber, balance);
            saveBalance(updatedBalance);
            log.info("Payment was successful an account: {}", accountNumber);
        } else {
            log.info("Payment was unsuccessful an account: {}", accountNumber);
        }
    }

    @Transactional
    public void cancelAuthorization(String accountNumber, BigDecimal amount) {
        Balance balance = getBalance(accountNumber);
        BigDecimal newAuthorizationBalance = balance.getAuthorizationBalance().add(amount);
        balance.setAuthorizationBalance(newAuthorizationBalance);
        saveBalance(balance);
        log.info("Payment authorization has been cancelled an account: {}", accountNumber);
    }

    public Balance getBalance(String accountNumber) {
        return balanceRepository.findByAccount_Number(accountNumber)
                .orElseThrow(() -> new EntityNotFoundException("Balance not found"));
    }

    private Balance saveBalance(Balance balance) {
        return balanceRepository.save(balance);
    }

}
