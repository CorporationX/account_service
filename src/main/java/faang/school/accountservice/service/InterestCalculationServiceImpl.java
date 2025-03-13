package faang.school.accountservice.service;

import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.repository.SavingsAccountRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterestCalculationServiceImpl implements InterestCalculationService {
    private static final Logger logger = LoggerFactory
            .getLogger(InterestCalculationServiceImpl.class);
    private final SavingsAccountRepository savingsAccountRepository;

    private final TariffService tariffService;

    @Override
    @Async
    @Retryable
    public void calculateInterest() {
        logger.info("Starting interest calculation for all savings accounts");
        List<SavingsAccount> accounts = savingsAccountRepository.findAll();
        for (SavingsAccount account : accounts) {
            applyInterest(account);
        }
        logger.info("Interest calculation process completed");
    }

    private void applyInterest(SavingsAccount account) {
        if (account.getTariffHistory().isEmpty()) {
            logger.warn("No tariff history found for account ID: {}", account.getId());
            return;
        }
        BigDecimal rate = getCurrentRate(account);
        BigDecimal interest = account.getBalance()
                .multiply(rate.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP));
        account.setBalance(account.getBalance().add(interest));
        account.setLastInterestCalculationDate(LocalDate.now());
        savingsAccountRepository.save(account);
        logger.info("Applied interest for account ID: {}, new balance: {}"
                , account.getId()
                , account.getBalance());
    }

    protected BigDecimal getCurrentRate(SavingsAccount account) {
        List<Long> tariffHistory = account.getTariffHistory();
        if (tariffHistory.isEmpty()) {
            logger.error("Account {} has empty tariff history", account.getId());
            throw new DataValidationException("Tariff history is empty for account: " + account.getId());
        }

        Long currentTariffId = tariffHistory.get(tariffHistory.size() - 1);
        Tariff currentTariff = tariffService.getTariff(currentTariffId)
                .orElseThrow(() -> {
                    String errorMsg = String.format("Tariff %d not found for account %d",
                            currentTariffId, account.getId());
                    logger.error(errorMsg);
                    return new DataValidationException(errorMsg);
                });

        List<Double> rateHistory = currentTariff.getRateHistory();
        if (rateHistory.isEmpty()) {
            String errorMsg = String.format("Tariff %d has empty rate history", currentTariffId);
            logger.error(errorMsg);
            throw new DataValidationException(errorMsg);
        }

        Double currentRate = rateHistory.get(rateHistory.size() - 1);
        return convertToBigDecimal(currentRate);
    }

    private BigDecimal convertToBigDecimal(Double rate) {
        try {
            // Конвертация с явным указанием точности
            return BigDecimal.valueOf(rate)
                    .setScale(4, RoundingMode.HALF_UP)
                    .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            logger.error("Invalid rate format: {}", rate);
            throw new DataValidationException("Invalid rate value: " + rate);
        }
    }

}
