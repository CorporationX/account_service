package faang.school.accountservice.service.impl;

import faang.school.accountservice.model.entity.SavingsAccount;
import faang.school.accountservice.model.entity.SavingsAccountRate;
import faang.school.accountservice.model.entity.Tariff;
import faang.school.accountservice.model.entity.TariffHistory;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRateRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffHistoryRepository;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.service.RateAdjustmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateAdjustmentServiceImpl implements RateAdjustmentService {
    private final SavingsAccountRepository savingsAccountRepository;
    private final TariffHistoryRepository tariffHistoryRepository;
    private final TariffRepository tariffRepository;
    private final SavingsAccountRateRepository savingsAccountRateRepository;
    private final AccountRepository accountRepository;

    @Value("${rate-change-rules.max-rate}")
    private double maxRate;

    @Override
    @Transactional
    public boolean adjustRate(long userId, double rateChange) {
        log.info("Starting rate adjustment for user ID {} with rate change: {}", userId, rateChange);

        List<String> accountNumbers = accountRepository.findNumbersByUserId(userId);
        List<SavingsAccount> savingsAccounts = savingsAccountRepository.findSaByAccountNumbers(accountNumbers);

        if (savingsAccounts.isEmpty()) {
            log.info("No SavingsAccount found for user ID {}. Rate adjustment aborted.", userId);
            return false;
        }

        List<SavingsAccountRate> newRateEntries = new ArrayList<>();
        List<TariffHistory> newTariffHistories = new ArrayList<>();

        for (SavingsAccount savingsAccount : savingsAccounts) {
            validateLastBonusUpdate(savingsAccount);

            Tariff tariff = getLatestTariffForSavingsAccount(savingsAccount);
            log.debug("Latest tariff found for savings account {}: {}", savingsAccount.getAccountNumber(), tariff);

            double currentRate = getCurrentRateForTariff(tariff);
            log.debug("Current rate for tariff ID {}: {}", tariff.getId(), currentRate);

            double newRate = calculateAdjustedRate(currentRate, rateChange);
            log.info("Calculated new rate for account {}: {}", savingsAccount.getAccountNumber(), newRate);

            updateSavingsAccountLastBonus(savingsAccount);
            newRateEntries.add(createNewRateEntry(tariff, newRate, rateChange));
            newTariffHistories.add(createNewHistoryEntry(savingsAccount, tariff));
        }

        saveAllEntities(savingsAccounts, newRateEntries, newTariffHistories);
        log.info("Rate adjustment completed for user ID {}", userId);
        return true;
    }

    private void validateLastBonusUpdate(SavingsAccount savingsAccount) {
        if (savingsAccount.getLastBonusUpdate() != null &&
                savingsAccount.getLastBonusUpdate().isAfter(LocalDateTime.now().minusDays(1))) {
            throw new IllegalStateException("Rate can only be adjusted once per 24 hours.");
        }
    }

    private Tariff getLatestTariffForSavingsAccount(SavingsAccount savingsAccount) {
        Long latestTariffId = tariffHistoryRepository.findLatestTariffIdBySavingsAccountId(savingsAccount.getId())
                .orElseThrow(() -> new IllegalStateException("No tariff history found for savings account: "
                        + savingsAccount.getAccountNumber()));

        return tariffRepository.findById(latestTariffId)
                .orElseThrow(() -> new EntityNotFoundException("Tariff not found for ID: " + latestTariffId));
    }

    private Double getCurrentRateForTariff(Tariff tariff) {
        return savingsAccountRateRepository.findLatestRateIdByTariffId(tariff.getId())
                .orElseThrow(() -> new EntityNotFoundException("No rate found for tariff ID: " + tariff.getId()));
    }

    private double calculateAdjustedRate(double currentRate, double rateChange) {
        double newRate = currentRate + rateChange;
        return Math.min(maxRate, Math.max(newRate, 0));
    }

    private void updateSavingsAccountLastBonus(SavingsAccount savingsAccount) {
        savingsAccount.setLastBonusUpdate(LocalDateTime.now());
    }

    private SavingsAccountRate createNewRateEntry(Tariff tariff, double newRate, double rateChange) {
        SavingsAccountRate newRateEntry = new SavingsAccountRate();
        newRateEntry.setTariff(tariff);
        newRateEntry.setRate(newRate);
        newRateEntry.setCreatedAt(LocalDateTime.now());
        newRateEntry.setRateBonusAdded(rateChange);
        return newRateEntry;
    }

    private TariffHistory createNewHistoryEntry(SavingsAccount savingsAccount, Tariff tariff) {
        TariffHistory historyEntry = new TariffHistory();
        historyEntry.setSavingsAccount(savingsAccount);
        historyEntry.setTariff(tariff);
        historyEntry.setCreatedAt(LocalDateTime.now());
        return historyEntry;
    }

    private void saveAllEntities(List<SavingsAccount> savingsAccounts, List<SavingsAccountRate> newRateEntries, List<TariffHistory> newTariffHistories) {
        savingsAccountRepository.saveAll(savingsAccounts);
        savingsAccountRateRepository.saveAll(newRateEntries);
        tariffHistoryRepository.saveAll(newTariffHistories);
    }
}
