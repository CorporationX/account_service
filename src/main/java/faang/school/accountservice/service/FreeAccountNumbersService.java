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
        AccountNumbersSequence accountNumbersSequence = null;
        try {
            accountNumbersSequence = accountNumbersSequenceRepository.getByType(type);
        } catch (NoSuchElementException e) {
            accountNumbersSequenceRepository.createAccountNumbersSequence(type);
            accountNumbersSequence = accountNumbersSequenceRepository.getByType(type);
        } finally {
            assert accountNumbersSequence != null;
            accountNumbersSequenceRepository.incrementAccountNumbersSequence(type);
            freeAccountNumbersRepository.createNewFreeNumber(accountNumbersSequence);
        }
    }

    @Transactional
    public void consumeFreeNumber(AccountType type, Consumer<String> numberConsumer) {
        FreeAccountNumber freeAccountNumber = freeAccountNumbersRepository.getFreeAccountNumber(type);

        if (freeAccountNumber == null) {
            createNewAccountNumber(type);
            freeAccountNumber = freeAccountNumbersRepository.getFreeAccountNumber(type);
        }

        numberConsumer.accept(freeAccountNumber.getNumber());
    }
}
