package faang.school.accountservice.service.account;

import faang.school.accountservice.exception.AccountOperationConflictException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Вспомогательный класс для операций, связанных с учетными записями.
 * Этот класс предоставляет вспомогательные методы для обработки операций с учетными записями, таких как
 * генерация номеров учетных записей, сохранение учетных записей и извлечение учетных записей по идентификатору.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountHelper {
    private final AccountRepository accountRepository;

    private static final int MAX_ATTEMPTS = 10;
    private static final int MIN_ACCOUNT_NUMBER_LENGTH = 12;
    private static final int MAX_ACCOUNT_NUMBER_LENGTH = 21;
    private static final int FIRST_DIGIT_MIN = 1;
    private static final int FIRST_DIGIT_MAX = 10;
    private static final int OTHER_DIGIT_MIN = 0;
    private static final int OTHER_DIGIT_MAX = 10;

    /**
     * Генерирует уникальный номер счёта, состоящий от 12 до 20 цифр.
     * Проверяет уникальность номера в базе данных.
     *
     * @return уникальный номер счёта
     * @throws IllegalStateException если не удалось сгенерировать уникальный номер после 10 попыток
     */
    public String generateUniqueAccountNumber() {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            String number = generateValidAccountNumber();

            if (!accountRepository.existsByAccountNumber(number)) {
                log.debug("Successfully generated unique account number [{}] on attempt {}", number, attempt);
                return number;
            }
        }
        log.error("Failed to generate unique account number after {} attempts", MAX_ATTEMPTS);
        throw new IllegalStateException("Failed to generate unique account number");
    }

    /**
     * Сохраняет учетную запись в репозитории.
     *
     * @param account учетная запись для сохранения
     * @return сохраненная учетная запись
     * @throws AccountOperationConflictException если возникает конфликт при сохранении учетной записи
     */
    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100)
    )
    public Account saveAccount(Account account) {
        log.debug("Attempting to save account {}", account.getId());
        return accountRepository.save(account);
    }

    private String generateValidAccountNumber() {
        int accountNumberLength = ThreadLocalRandom.current().nextInt(MIN_ACCOUNT_NUMBER_LENGTH, MAX_ACCOUNT_NUMBER_LENGTH);
        StringBuilder accountNumberBuilder = new StringBuilder();

        accountNumberBuilder.append(ThreadLocalRandom.current().nextInt(FIRST_DIGIT_MIN, FIRST_DIGIT_MAX));

        for (int i = 1; i < accountNumberLength; i++) {
            accountNumberBuilder.append(ThreadLocalRandom.current().nextInt(OTHER_DIGIT_MIN, OTHER_DIGIT_MAX));
        }

        return accountNumberBuilder.toString();
    }
}