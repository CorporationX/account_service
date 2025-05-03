package faang.school.accountservice.service.account;

import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountOperationConflictException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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
     * Получает учетную запись по идентификатору.
     *
     * @param accountId идентификатор учетной записи
     * @return учетная запись
     * @throws AccountNotFoundException если учетная запись не найдена
     */
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found with id: {}", accountId);
                    return new AccountNotFoundException(String.format("Account not found with id: %d", accountId));
                });
    }

    /**
     * Генерирует уникальный номер счёта, состоящий от 12 до 20 цифр.
     * Проверяет уникальность номера в базе данных.
     *
     * @return уникальный номер счёта
     * @throws IllegalStateException если не удалось сгенерировать уникальный номер после 10 попыток
     */
    public String generateAccountNumber() {
        int attempt = 0;
        String number;
        do {
            if (attempt++ > MAX_ATTEMPTS) {
                log.error("Failed to generate unique account number after {} attempts", MAX_ATTEMPTS);
                throw new IllegalStateException("Failed to generate unique account number");
            }

            int length = ThreadLocalRandom.current().nextInt(MIN_ACCOUNT_NUMBER_LENGTH, MAX_ACCOUNT_NUMBER_LENGTH);

            StringBuilder sb = new StringBuilder();
            sb.append(ThreadLocalRandom.current().nextInt(FIRST_DIGIT_MIN, FIRST_DIGIT_MAX));
            for (int i = 1; i < length; i++) {
                sb.append(ThreadLocalRandom.current().nextInt(OTHER_DIGIT_MIN, OTHER_DIGIT_MAX));
            }
            number = sb.toString();

        } while (accountRepository.existsByAccountNumber(number));

        return number;
    }

    /**
     * Сохраняет учетную запись в репозитории.
     *
     * @param account      учетная запись для сохранения
     * @return сохраненная учетная запись
     * @throws AccountOperationConflictException если возникает конфликт при сохранении учетной записи
     */
    public Account saveAccount(Account account) {
        try {
            return accountRepository.save(account);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while saving account: {}", account, e);
            throw new AccountOperationConflictException("Optimistic locking failure while saving account");
        }
    }
}