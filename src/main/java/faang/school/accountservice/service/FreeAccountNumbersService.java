package faang.school.accountservice.service;

import faang.school.accountservice.exception.AccountNumberGenerationException;
import faang.school.accountservice.model.AccountNumbersSequence;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.enums.AccountType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeAccountNumbersService {

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Transactional
    public void getAndRemoveFreeAccountNumber(AccountType accountType, Consumer<String> onSuccess) {
        log.info("Попытка получить свободный номер счета для типа: {}", accountType);

        Optional<FreeAccountNumber> freeNumberOpt = freeAccountNumbersRepository.findFirstByAccountType(accountType);

        if (freeNumberOpt.isPresent()) {
            FreeAccountNumber freeAccountNumber = freeNumberOpt.get();
            freeAccountNumbersRepository.delete(freeAccountNumber);
            log.info("Найден и удален свободный номер счета: {}", freeAccountNumber.getAccountNumber());
            onSuccess.accept(freeAccountNumber.getAccountNumber());
        } else {
            log.warn("Свободный номер счета для типа {} не найден, генерируем новый номер.", accountType);
            String generatedNumber = generateNewAccountNumber(accountType);
            log.info("Сгенерирован новый номер счета: {}", generatedNumber);
            onSuccess.accept(generatedNumber);
        }
    }


    private String generateNewAccountNumber(AccountType accountType) {
        log.info("Попытка получить или создать последовательность для типа счета: {}", accountType);

        AccountNumbersSequence sequence = getOrCreateSequence(accountType);

        log.info("Текущая последовательность для типа счета {}: {}", accountType, sequence);

        boolean incremented = incrementSequence(accountType, sequence);

        if (!incremented) {
            log.error("Не удалось инкрементировать счетчик для типа счета: {}. Возможно, версия счетчика устарела.", accountType);
            throw new AccountNumberGenerationException("Не удалось инкрементировать счетчик для типа счета: " + accountType.name(), accountType);
        }

        String prefix = accountType.getPrefix();

        String accountNumber = prefix + String.format("%012d", sequence.getCurrentValue());
        log.info("Новый номер счета для типа {}: {}", accountType, accountNumber);

        return accountNumber;
    }

    private AccountNumbersSequence getOrCreateSequence(AccountType accountType) {
        return accountNumbersSequenceRepository.findByAccountType(accountType)
            .orElseGet(() -> createNewAccountNumberSequence(accountType));
    }

    private boolean incrementSequence(AccountType accountType, AccountNumbersSequence sequence) {
        return accountNumbersSequenceRepository.incrementCounter(accountType, sequence.getVersion()) > 0;
    }

    private AccountNumbersSequence createNewAccountNumberSequence(AccountType accountType) {
        try {
            log.info("Создание новой последовательности счета для типа: {}", accountType);

            AccountNumbersSequence newSequence = AccountNumbersSequence.builder()
                .accountType(accountType)
                .currentValue(0L)
                .version(1L)
                .build();

            AccountNumbersSequence savedSequence = accountNumbersSequenceRepository.save(newSequence);
            log.info("Новая последовательность счета для типа {} создана: {}", accountType, savedSequence);
            return savedSequence;
        } catch (Exception e) {
            log.error("Ошибка при создании новой последовательности счета для типа: {}", accountType, e);
            throw new AccountNumberGenerationException("Ошибка при создании новой последовательности счета", accountType);
        }
    }
}
