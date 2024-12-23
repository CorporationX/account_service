
package faang.school.accountservice.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import faang.school.accountservice.model.account.AccountType;
import faang.school.accountservice.model.account.freeaccounts.FreeAccountNumber;
import faang.school.accountservice.model.account.sequence.AccountSeq;
import faang.school.accountservice.repository.account.freeaccounts.FreeAccountRepository;
import faang.school.accountservice.repository.account.sequence.AccountNumbersSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumberService {
    private final FreeAccountRepository freeAccountRepository;

    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private static final long BASE_ACCOUNT_NUMBER = 4200000000000000L;

    private static final Logger log = LoggerFactory.getLogger(FreeAccountNumberService.class);
    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public void processAccountNumber(String type, Consumer<FreeAccountNumber> action) {
        AccountType accountType = AccountType.valueOf(type.toUpperCase());

        // Пытаемся извлечь и удалить первый свободный номер
        log.info("Attempting to retrieve and delete first free account number for type: {}", accountType.name());
        Optional<FreeAccountNumber> freeAccount = freeAccountRepository.retrieveAndDeleteFirst(accountType.name());
        if (freeAccount.isPresent()) {
            log.info("Account number retrieved and deleted: {}", freeAccount.get().getAccountNumber());
            action.accept(freeAccount.get());
            return;
        }

        log.warn("No free account number found for type: {}", accountType.name());

        // Проверяем или создаём счётчик
        AccountSeq accountSeq = accountNumbersSequenceRepository.findByType(accountType)
                .orElseGet(() -> {
                    log.warn("Counter not found for type: {}. Creating a new counter.", accountType.name());
                    return accountNumbersSequenceRepository.createCounter(accountType);
                });

        Long currentCounter = accountSeq.getCounter();
        log.info("Current counter for type {}: {}", accountType.name(), currentCounter);

        // Инкрементируем счётчик
        int updatedRows = accountNumbersSequenceRepository.incrementCounter(accountType, currentCounter);
        if (updatedRows > 0) {
            Long newCounterValue = currentCounter + 1;
            String newAccountNumber = BASE_ACCOUNT_NUMBER + newCounterValue.toString();

            log.info("Counter successfully incremented. New counter value: {}", newCounterValue);

            // Создаём новый номер
            FreeAccountNumber newAccount = freeAccountRepository.save(
                    new FreeAccountNumber(null, accountType, newAccountNumber)
            );
            log.info("New account number created and saved: {}", newAccountNumber);

            // Сразу удаляем номер и обрабатываем
            freeAccountRepository.delete(newAccount);
            log.info("Account number deleted after creation: {}", newAccountNumber);
            action.accept(newAccount);
        } else {
            log.error("Failed to increment counter for type: {}. Possible conflict on counter: {}", accountType.name(), currentCounter);
            throw new IllegalStateException("Failed to increment counter for type: " + type);
        }
    }

    private void logCurrentState(String type) {
        List<Object[]> results = entityManager.createNativeQuery(
                "SELECT id, type, account_number FROM free_account_numbers WHERE type = :type"
        ).setParameter("type", type).getResultList();

        results.forEach(row -> log.info("Row in DB: id={}, type={}, account_number={}", row[0], row[1], row[2]));
    }
}
