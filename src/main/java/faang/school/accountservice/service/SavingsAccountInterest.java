package faang.school.accountservice.service;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.repository.BalanceJpaRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class SavingsAccountInterest {

    private final SavingsAccountRepository savingsAccountRepository;
    private final BalanceJpaRepository balanceRepository;

    @Value("${interest.accrual.days}")
    private Integer amountDays;

    @Value("${interest.accrual.hours}")
    private Integer amountHours;

    @Transactional
    public void processInterestAccrual(SavingsAccount savingsAccount) {
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
