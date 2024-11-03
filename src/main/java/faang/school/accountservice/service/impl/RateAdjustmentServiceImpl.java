package faang.school.accountservice.service.impl;

import faang.school.accountservice.model.entity.SavingsAccount;
import faang.school.accountservice.model.entity.SavingsAccountRate;
import faang.school.accountservice.model.entity.TariffHistory;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffHistoryRepository;
import faang.school.accountservice.service.RateAdjustmentService;
import faang.school.accountservice.util.RateAdjustmentRulesLoader;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateAdjustmentServiceImpl implements RateAdjustmentService {
    private final SavingsAccountRepository savingsAccountRepository;
    private final TariffHistoryRepository tariffHistoryRepository;
    private final RateAdjustmentRulesLoader rulesLoader;

    @Override
    public void adjustRateForAchievement(long userId, String achievementTitle) {
        // 1. Получаем правило увеличения ставки
        Double increaseRate = rulesLoader.getIncreaseRate(achievementTitle);
        if (increaseRate == null) {
            log.warn("No increase rate rule found for achievement: " + achievementTitle);
            return;
        }

        // 2. Ищем накопительный счёт пользователя
        SavingsAccount account = savingsAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Savings account not found for user: " + userId));

        // 3. Получаем текущий бонус к ставке
        BigDecimal currentBonus = account.getRateBonus() != null ? account.getRateBonus() : BigDecimal.ZERO;
        BigDecimal newBonus = currentBonus.add(BigDecimal.valueOf(increaseRate));
        account.setRateBonus(newBonus);
        account.setLastBonusUpdate(LocalDateTime.now());

        // 4. Получаем базовую ставку из SavingsAccountRate
        SavingsAccountRate currentRate = account.getAccount().getSavingsAccountRates()
                .stream()
                .max(Comparator.comparing(SavingsAccountRate::getCreatedAt))
                .orElseThrow(() -> new EntityNotFoundException("No rate found for account: " + account.getAccountNumber()));

        // 5. Рассчитываем новую ставку с учётом бонуса
        BigDecimal baseRate = BigDecimal.valueOf(currentRate.getRate());
        BigDecimal newRate = baseRate.add(newBonus);

        // 6. Обновляем ставку в SavingsAccount
        account.getAccount().setCurrentRate(newRate.doubleValue()); // Предположим, что есть поле для текущей ставки

        // 7. Создаём запись в TariffHistory
        TariffHistory history = new TariffHistory();
        history.setSavingsAccount(account);
        history.setTariff(currentRate.getTariff());
        history.setCreatedAt(LocalDateTime.now());
        tariffHistoryRepository.save(history);

        // 8. Сохраняем изменения в SavingsAccount
        savingsAccountRepository.save(account);

        log.info("Rate adjusted for user {}. New rate: {}", userId, newRate);
    }

    private double getMaxRate() {
        // Получение максимальной ставки из конфигурации
        return 5.0; // Например, 5%
    }
}
