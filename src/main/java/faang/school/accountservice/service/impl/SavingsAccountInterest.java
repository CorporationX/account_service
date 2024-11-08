package faang.school.accountservice.service.impl;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.repository.BalanceJpaRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Data
@Component
@RequiredArgsConstructor
public class SavingsAccountInterest {

    private final SavingsAccountRepository savingsAccountRepository;
    private final BalanceJpaRepository balanceRepository;

    @Value("${interest.accrual.days}")
    private Integer amountDays;

    @Value("${interest.accrual.hours}")
    private Integer amountHours;

    @Value(value = "${poolThreads.poolForAccrual}")
    private Integer poolThreads;

    @Scheduled(cron = "${scheduled.task.cronForAccrual}")
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 5000))
    @Transactional
    public void interestAccrual() {
        List<SavingsAccount> accounts = savingsAccountRepository.findAll();
        ExecutorService executor = Executors.newFixedThreadPool(poolThreads);

        for (SavingsAccount savingsAccount : accounts) {
            executor.submit(() -> processInterestAccrual(savingsAccount));
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }


    private void processInterestAccrual(SavingsAccount savingsAccount) {
        Balance balance = savingsAccount.getAccount().getBalance();

        if (ChronoUnit.HOURS.between(savingsAccount.getLastDateBeforeInterest(), LocalDateTime.now()) >= amountHours) {
            BigDecimal currentTotal = calculationsTotalAmount(savingsAccount, balance);

            balance.setCurFactBalance(currentTotal);
            balanceRepository.save(balance);

            savingsAccount.setLastDateBeforeInterest(LocalDateTime.now());
            savingsAccountRepository.save(savingsAccount);
        }
    }

    private BigDecimal calculationsTotalAmount(SavingsAccount savingsAccount, Balance balance) {
        BigDecimal dailyRate = savingsAccount.getTariff().getCurrentRate().divide(BigDecimal.valueOf(amountDays), RoundingMode.HALF_UP);
        return balance.getCurFactBalance().multiply(dailyRate).add(balance.getCurFactBalance());
    }
}
