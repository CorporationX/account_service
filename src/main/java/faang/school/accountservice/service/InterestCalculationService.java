package faang.school.accountservice.service;

import faang.school.accountservice.model.savings.SavingsAccount;
import faang.school.accountservice.repository.SavingsAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterestCalculationService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final ExecutorService interestCalculationThreadPool;

    @Value("${account.interest.scheduler.batch.size}")
    private int batchSize;

    public void calculateDailyInterest() {
        List<SavingsAccount> accounts = savingsAccountRepository.findAll();

        for (int i = 0; i < accounts.size(); i += batchSize) {
            int end = Math.min(i + batchSize, accounts.size());
            List<SavingsAccount> batch = accounts.subList(i, end);

            interestCalculationThreadPool.submit(() -> processInterestForBatch(batch));
        }
    }

    @Transactional
    public void processInterestForBatch(List<SavingsAccount> batch) {
        for (SavingsAccount account : batch) {
            try {
                calculateInterestForAccount(account);
            } catch (Exception e) {
                log.error("Ошибка при начислении процентов для аккаунта {}: {}", account.getId(), e.getMessage());
            }
        }
        savingsAccountRepository.saveAll(batch);
    }

    private void calculateInterestForAccount(SavingsAccount account) {
        double interestRate = getLatestRate(account);

        LocalDateTime lastCalculatedAt = account.getLastCalculatedAt();
        LocalDateTime now = LocalDateTime.now();

        if (lastCalculatedAt == null || lastCalculatedAt.isBefore(now.minusDays(1))) {
            double interestAmount = calculateInterest(account, interestRate);
            updateAccountBalance(account, interestAmount);
            account.setLastCalculatedAt(now);
        }
    }

    private double getLatestRate(SavingsAccount account) {
        return account.getTariffHistory().stream()
                .map(history -> history.getTariff().getRateHistory())
                .flatMap(List::stream)
                .mapToDouble(rate -> rate.getRate())
                .max()
                .orElse(0.0);
    }

    private double calculateInterest(SavingsAccount account, double interestRate) {
        // TODO: Заменить мок на реальное значение баланса, когда оно будет добавлено в Account
        double mockBalance = 1000.0;
        return mockBalance * interestRate / 100;
    }

    private void updateAccountBalance(SavingsAccount account, double interestAmount) {
        // TODO: Обновить реальный баланс
        log.info("Добавлено {} к балансу аккаунта ID: {}", interestAmount, account.getId());
    }
}