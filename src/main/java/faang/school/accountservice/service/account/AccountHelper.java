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
            if (attempt++ > 10) {
                throw new IllegalStateException("Не удалось сгенерировать уникальный номер счёта");
            }

            int length = ThreadLocalRandom.current().nextInt(12, 21);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(ThreadLocalRandom.current().nextInt(i == 0 ? 1 : 0, 10));
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
    public Account B(Account account) {
        try {
            return accountRepository.save(account);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while saving account: {}", account, e);
            throw new AccountOperationConflictException("Optimistic locking failure while saving account");
        }
    }
}