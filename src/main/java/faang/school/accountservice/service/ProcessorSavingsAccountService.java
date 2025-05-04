package faang.school.accountservice.service;

import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.exception.RetryableException;
import faang.school.accountservice.exception.TariffNotFoundException;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.concurrent.CompletionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessorSavingsAccountService {

    private static final int MULTIPLIER_BY_SCALE = 4;
    private static final int AMOUNT_BY_SCALE = 2;
    private static final int DIVIDER = 100;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final int RETRY_DELAY = 500;
    private static final int RETRY_MULTIPLIER = 3;

    private final SavingsAccountRepository savingsAccountRepository;
    private final TariffRepository tariffRepository;

    @Retryable(
            retryFor = RetryableException.class,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER)
    )
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void processSingleAccount(SavingsAccount account) {
        try {
            String activeTypeName = savingsAccountRepository.findLatestTariffTypeNameByAccountId(account.getAccountId())
                    .orElseThrow(() -> new TariffNotFoundException("Tariff not found"));
            BigDecimal activeRate = tariffRepository.findLatestRateByTariffTypeName(activeTypeName)
                    .orElseThrow(() -> new TariffNotFoundException("Tariff rate not found"));

            BigDecimal balance = account.getBalance();
            BigDecimal rateMultiplier = activeRate.divide(
                    BigDecimal.valueOf(DIVIDER), MULTIPLIER_BY_SCALE, ROUNDING_MODE);
            BigDecimal interestAmount = balance.multiply(rateMultiplier).setScale(AMOUNT_BY_SCALE, ROUNDING_MODE);
            BigDecimal newBalance = balance.add(interestAmount);
            account.setBalance(newBalance);
            account.setLastInterestAccrualAt(LocalDateTime.now());

            savingsAccountRepository.save(account);
            log.debug("Savings account {} balance recalculated", account.getAccountNumber());
        } catch (DataAccessException | TransactionException e) {
            log.warn("Retryable error processing account {}: {}", account.getAccountNumber(), e.getMessage());
            throw new RetryableException(e.getMessage(), e);
        } catch (Exception ex) {
            throw new CompletionException("Error processing account " + account.getAccountNumber(), ex);
        }
    }
}
