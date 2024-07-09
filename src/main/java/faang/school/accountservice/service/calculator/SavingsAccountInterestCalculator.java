package faang.school.accountservice.service.calculator;

import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.model.SavingsAccountTariffHistory;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.TariffRateHistory;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.service.BalanceService;
import faang.school.accountservice.service.TariffService;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class SavingsAccountInterestCalculator {
    private final SavingsAccountRepository savingsAccountRepository;
    private final BalanceService balanceService;
    private final TariffService tariffService;
    private final ExecutorService executorService;

    @Scheduled(cron = "${schedulers.savings_accounts.interest_calculation.cron}")
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public void calculateInterestForSavingsAccounts() {
        List<CompletableFuture<Void>> futures = savingsAccountRepository.findAll().stream()
                .parallel()
                .map(savingsAccount -> CompletableFuture.runAsync(() -> calculateAndUpdateInterest(savingsAccount), executorService))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .join();
    }

    protected void calculateAndUpdateInterest(SavingsAccount savingsAccount){
        LocalDate lastInterestCalculatedDate = savingsAccount.getLastInterestCalculatedDate().toLocalDate();
        LocalDate currentDate = LocalDate.now();
        if (lastInterestCalculatedDate.isBefore(currentDate)) {
            SavingsAccountTariffHistory currentTariffHistory = getCurrentTariffHistory(savingsAccount);
            Tariff currentTariff = currentTariffHistory.getTariff();
            BigDecimal currentRate = getCurrentRate(currentTariff);

            Account account = savingsAccount.getAccount();
            Balance balance = account.getBalance();
            BigDecimal currentBalance = balance.getCurrentBalance();

            LocalDate startDate = lastInterestCalculatedDate.plusDays(1);
            long daysCount = ChronoUnit.DAYS.between(startDate, currentDate);
            BigDecimal interest = calculateInterest(currentBalance, currentRate, daysCount);
            BigDecimal newBalance = currentBalance.add(interest);

            balanceService.updateBalance(account.getId(), newBalance, balance.getAuthorizedBalance());
            savingsAccount.setLastInterestCalculatedDate(LocalDateTime.now());
            savingsAccountRepository.save(savingsAccount);
        }
    }

    private SavingsAccountTariffHistory getCurrentTariffHistory(SavingsAccount savingsAccount){
        List<SavingsAccountTariffHistory> tariffHistory = savingsAccount.getTariffHistory();
        return tariffHistory.stream()
                .filter(history -> history.getEndDate() == null)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Current tariff history not found"));
    }

    private BigDecimal getCurrentRate(Tariff tariff){
        List<TariffRateHistory> rateHistory = tariff.getRateHistory();
        TariffRateHistory currentRate = rateHistory.stream()
                .max(Comparator.comparing(TariffRateHistory::getCreatedAt))
                .orElseThrow(() -> new ResourceNotFoundException("Current rate not found"));
        return currentRate.getRate();
    }

    private BigDecimal calculateInterest(BigDecimal balance, BigDecimal rate, long daysCount) {
        BigDecimal ratePerDay = rate.divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP);
        return balance.multiply(ratePerDay).multiply(BigDecimal.valueOf(daysCount));
    }
}
