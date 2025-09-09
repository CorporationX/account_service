package faang.school.accountservice.service;

import faang.school.accountservice.config.property.BalanceProps;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.repository.SavingsAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestService {
    private final BalanceProps balanceProps;
    private final RateService rateService;
    private final SavingsAccountRepository savingsAccountRepository;

    @Transactional
    @Retryable(noRetryFor = {EntityNotFoundException.class},
            maxAttemptsExpression = "${retry.account.accrueInterest.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.account.accrueInterest.delay}"))
    public void accrueInterest() {
        List<SavingsAccount> accounts = savingsAccountRepository.findAllForAccrueInterest();
        accounts.forEach(this::accrueInterestToAccount);
    }

    private void accrueInterestToAccount(SavingsAccount account) {
        log.info("Start accrual interest for account id = {}", account.getId());
        BigDecimal currentRate = rateService.findCurrentRateByAccountId(account.getId());
        BigDecimal balance = account.getBalance();
        BigDecimal newBalance = calculateNewBalance(balance, currentRate);
        account.setBalance(newBalance);
        account.setLastInterestAt(LocalDateTime.now());
        log.info("Accrual interest for account id = {} completed", account.getId());
    }

    private BigDecimal calculateNewBalance(BigDecimal balance, BigDecimal rate) {
        log.debug("Calculate interest: balance = {}, rate = {}", balance, rate);
        BigDecimal interestAmount = calculateDailyInterest(balance, rate);
        return balance.add(interestAmount);
    }

    private BigDecimal calculateDailyInterest(BigDecimal balance, BigDecimal rate) {
        log.debug("Calculate daily interest: balance = {}, rate = {}", balance, rate);
        BigDecimal daysOfYear = BigDecimal.valueOf(Year.now().isLeap() ? 366 : 365);
        return balance
                .multiply(rate.divide(daysOfYear, balanceProps.divideScale(), balanceProps.roundingMode()))
                .setScale(balanceProps.scale(), balanceProps.roundingMode());
    }
}
