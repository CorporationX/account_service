package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Transactional
    public void createNewAccountNumber(AccountType type) {
        AccountNumbersSequence accountNumbersSequence = accountNumbersSequenceRepository.findOrCreateByType(type);

        accountNumbersSequenceRepository.incrementAccountNumbersSequence(accountNumbersSequence);
        freeAccountNumbersRepository.createNewFreeNumber(accountNumbersSequence);
    }


    /**
     * Метод для использования свободного номера аккаунта. Используется при создании нового аккаунта.
     *
     * @param type           тип счета
     * @param numberConsumer операция, поглощающая свободный номер
     */
    @Transactional
    public void consumeFreeNumber(AccountType type, Consumer<String> numberConsumer) {
        FreeAccountNumber freeAccountNumber = freeAccountNumbersRepository.getFreeAccountNumber(type)
                .orElseGet(() -> createAndGetFreeNumber(type));

        numberConsumer.accept(freeAccountNumber.getNumber());
    }

    private FreeAccountNumber createAndGetFreeNumber(AccountType type) {
        createNewAccountNumber(type);
        return freeAccountNumbersRepository.getFreeAccountNumber(type)
                .orElseThrow(() -> {
                    String message = String.format("There is no free number for type %s", type.name());
                    return new NoSuchElementException(message);
                });
    }
}
