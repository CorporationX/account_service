package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountNumberScheduler {

    @Value("${account.number.batch.size}")
    private int batchSize;

    private final FreeAccountNumbersService freeAccountNumbersService;

    @Scheduled(cron = "0 0 0 * * *")
    public void generatePersonal() {
        try {
            log.info("Запуск генерации номеров для типа счета: PERSONAL с размером партии: {}", batchSize);
            freeAccountNumbersService.generateFreeAccountNumbers(AccountType.PERSONAL, batchSize);
            log.info("Генерация номеров для типа счета: PERSONAL завершена успешно.");
        } catch (Exception e) {
            log.error("Ошибка при генерации номеров для типа счета: PERSONAL", e);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateBusiness() {
        try {
            log.info("Запуск генерации номеров для типа счета: BUSINESS с размером партии: {}", batchSize);
            freeAccountNumbersService.generateFreeAccountNumbers(AccountType.BUSINESS, batchSize);
            log.info("Генерация номеров для типа счета: BUSINESS завершена успешно.");
        } catch (Exception e) {
            log.error("Ошибка при генерации номеров для типа счета: BUSINESS", e);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateCurrency() {
        try {
            log.info("Запуск генерации номеров для типа счета: CURRENCY с размером партии: {}", batchSize);
            freeAccountNumbersService.generateFreeAccountNumbers(AccountType.CURRENCY, batchSize);
            log.info("Генерация номеров для типа счета: CURRENCY завершена успешно.");
        } catch (Exception e) {
            log.error("Ошибка при генерации номеров для типа счета: CURRENCY", e);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateSavings() {
        try {
            log.info("Запуск генерации номеров для типа счета: SAVINGS с размером партии: {}", batchSize);
            freeAccountNumbersService.generateFreeAccountNumbers(AccountType.SAVINGS, batchSize);
            log.info("Генерация номеров для типа счета: SAVINGS завершена успешно.");
        } catch (Exception e) {
            log.error("Ошибка при генерации номеров для типа счета: SAVINGS", e);
        }
    }
}
