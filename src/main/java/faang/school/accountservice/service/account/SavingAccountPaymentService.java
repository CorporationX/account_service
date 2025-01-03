package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.account.SavingAccount;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.repository.account.SavingAccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SavingAccountPaymentService {
    private final SavingAccountRepository savingAccountRepository;
    private final AccountRepository accountRepository;
    @Value("${task.saving-account-payments.max-rate}")
    private BigDecimal maxRate;
    @Value("${task.saving-account-payments.min-rate}")
    private BigDecimal minRate;
    @Value("${task.saving-account-bonuses.bonus-threshold-rate}")
    private BigDecimal bonusRate;
    @Value("${task.saving-account-bonuses.bonus-threshold}")
    private int bonusThreshold;

    @Transactional
    @Async("savingAccountTaskExecutor")
    @Retryable(retryFor = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 3000))
    public void payOffInterests(List<SavingAccount> accounts) {
        LocalDateTime now = LocalDateTime.now();
        for (var account : accounts) {
            account.setIncreasedAt(now);
            BigDecimal rate = calculateRate(account.getTariff().getRate(), account.getBonuses());
            BigDecimal oldBalance = account.getAccount().getBalance();
            BigDecimal newBalance = oldBalance.add(oldBalance.multiply(rate));
            account.getAccount().setBalance(newBalance);
            log.info("Balance increased for saving account '{}'", account.getAccount().getPaymentNumber());
        }
        accountRepository.saveAll(accounts.stream().map(SavingAccount::getAccount).toList());
        savingAccountRepository.saveAll(accounts);
    }

    private BigDecimal calculateRate(BigDecimal baseRate, Integer bonuses) {
        int countThreshold = bonuses / bonusThreshold;
        BigDecimal rate = bonusRate.multiply(BigDecimal.valueOf(countThreshold));
        rate = baseRate.add(rate);

        if (rate.compareTo(maxRate) > 0) {
            rate = maxRate;
        }
        if (rate.compareTo(minRate) < 0) {
            rate = minRate;
        }

        return rate;
    }

    @Recover
    public void recoverException(Exception e) {
        log.error("Pay off interest error", e);
    }
}
