package faang.school.accountservice.service.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import faang.school.accountservice.entity.account.AccountNumbersSequence;
import faang.school.accountservice.enums.account.AccountEnum;
import faang.school.accountservice.repository.account.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.account.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    /**
     * Метод для добавления нового свободного номера счета.
     */
    public void addFreeAccountNumber(AccountEnum accountType, Long freeAccountNumber) {
        freeAccountNumbersRepository.createFreeAccountNumber(accountType, freeAccountNumber);
    }

    public AccountNumbersSequence createCounterForAccountType(AccountEnum accountType) {
        AccountNumbersSequence sequence = new AccountNumbersSequence();
        sequence.setAccountType(accountType);
        sequence.setCurrentCounter(0L);
        return accountNumbersSequenceRepository.save(sequence);
    }

    /**
     * Метод для получения нового свободного номера счета и выполнения лямбды с ним.
     */
    @Transactional
    public void processAccountNumber(AccountEnum accountType, Consumer<Long> action) {
        // Шаг 1: Попытка получить и удалить свободный номер счета
        Long accountNumber = freeAccountNumbersRepository.findAndRemoveFreeAccountNumber(accountType);

        // Шаг 2: Если свободного номера нет, генерируем новый через инкремент счетчика
        if (accountNumber == null) {
            accountNumber = generateNewAccountNumber(accountType);
        }

        // Шаг 3: Выполнение переданной лямбды с полученным номером счета
        if (accountNumber != null) {
            action.accept(accountNumber);
        } else {
            throw new IllegalStateException("Failed to generate or retrieve an account number");
        }
    }

    /**
     * Генерация нового номера счета, если свободных номеров не оказалось.
     */
    private Long generateNewAccountNumber(AccountEnum accountType) {
        // Загружаем или создаем счетчик для данного типа счета
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findById(accountType)
            .orElseGet(() -> accountNumbersSequenceRepository.createCounterForAccountType(accountType));

        // Попытка инкрементации счетчика
        boolean hasIncremented = accountNumbersSequenceRepository.incrementCounterForAccountType(accountType, sequence.getCurrentCounter());

        if (hasIncremented) {
            // Если инкрементация успешна, формируем новый номер счета
            long newAccountNumber = Long.parseLong(accountType.getPrefix() + String.format("%08d", sequence.getCurrentCounter() + 1));
            addFreeAccountNumber(accountType, newAccountNumber); // сохраняем номер в качестве нового свободного
            return newAccountNumber;
        }
        return null;
    }
}
