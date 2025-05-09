package faang.school.accountservice.service.savingsaccount;

import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.exception.InterestCalculationException;
import faang.school.accountservice.exception.TariffNotFoundException;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterestCalculationService {
    private static final int THREAD_POOL_SIZE = 4;
    private static final int DAYS_IN_YEAR = 365;
    private static final int CALCULATION_SCALE = 10;
    private static final int RESULT_SCALE = 2;
    private static final long SHUTDOWN_TIMEOUT_HOURS = 1;

    private final SavingsAccountRepository savingsAccountRepository;
    private final TariffRepository tariffRepository;

    @Scheduled(cron = "${app.calculate-daily-interest.schedule.cron}")
    @Retryable(
            retryFor = {OptimisticLockException.class},
            maxAttemptsExpression = "${app.optimistic-lock-max-attempts}",
            backoff = @Backoff(delayExpression = "${app.optimistic-lock-backoff-delay}")
    )
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void calculateDailyInterest() {
        log.info("Starting daily interest calculation");
        try {
            List<SavingsAccount> accounts = savingsAccountRepository.findAllActiveAccountsForInterestCalculation();
            if (accounts.isEmpty()) {
                log.info("No accounts eligible for interest calculation");
                return;
            }

            log.info("Found {} accounts for interest calculation", accounts.size());
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            List<Future<?>> futures = processAccounts(accounts, executor);
            waitForCompletion(futures, executor);
        } catch (Exception e) {
            String message = "Failed to complete interest calculation";
            log.error(message, e);
            throw new InterestCalculationException(message, e);
        }
    }

    private List<Future<?>> processAccounts(List<SavingsAccount> accounts, ExecutorService executor) {
        List<Future<?>> futures = new ArrayList<>();

        for (SavingsAccount account : accounts) {
            futures.add(executor.submit(() -> {
                try {
                    calculateInterestForAccount(account);
                } catch (Exception e) {
                    log.error("Failed to calculate interest for account {}: {}",
                            account.getId(), e.getMessage());
                }
            }));
        }

        return futures;
    }

    private void waitForCompletion(List<Future<?>> futures, ExecutorService executor) {
        executor.shutdown();

        try {
            for (Future<?> future : futures) {
                future.get();
            }

            if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_HOURS, TimeUnit.HOURS)) {
                log.warn("Forcing executor shutdown after timeout");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();
        } catch (ExecutionException e) {
            log.error("Error during interest calculation execution", e.getCause());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void calculateInterestForAccount(SavingsAccount account) {
        try {
            Tariff tariff = getValidTariff(account);
            BigDecimal interest = calculateInterestAmount(account, tariff);

            if (interest.compareTo(BigDecimal.ZERO) > 0) {
                updateAccountWithInterest(account, interest);
                log.debug("Applied interest {} to account {}", interest, account.getId());
            }
        } catch (Exception e) {
            log.error("Interest calculation failed for account {}: {}",
                    account.getId(), e.getMessage(), e);
            throw new InterestCalculationException(
                    "Failed to calculate interest for account " + account.getId(), e);
        }
    }

    private Tariff getValidTariff(SavingsAccount account) {
        return tariffRepository.findById(account.getCurrentTariffId())
                .orElseThrow(() -> new TariffNotFoundException(
                        "Tariff not found for account " + account.getId()));
    }

    private BigDecimal calculateInterestAmount(SavingsAccount account, Tariff tariff) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastDate = Optional.ofNullable(account.getLastInterestDate())
                .orElse(account.getCreatedAt());

        long days = ChronoUnit.DAYS.between(lastDate, now);
        if (days <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal dailyRate = tariff.getCurrentRate()
                .divide(BigDecimal.valueOf(DAYS_IN_YEAR), CALCULATION_SCALE, RoundingMode.HALF_UP);

        return account.getBalance()
                .multiply(dailyRate)
                .multiply(BigDecimal.valueOf(days))
                .setScale(RESULT_SCALE, RoundingMode.HALF_UP);
    }

    private void updateAccountWithInterest(SavingsAccount account, BigDecimal interest) {
        account.setBalance(account.getBalance().add(interest));
        account.setLastInterestDate(LocalDateTime.now());
    }
}
