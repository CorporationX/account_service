package faang.school.accountservice.service.account.numbers;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
@Slf4j
class NumbersSequenceService {
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final int MAX_NUMBER_OF_FREE_ACCOUNTS = 1000;
    private final int MIN_NUMBER_OF_FREE_ACCOUNTS = 100;
    private final int MAX_DIGIT_OF_NUMBER = 20;


    @Async
    @Transactional
    public void checkForGenerationSequencesAsync(AccountNumberType type) {
        long amountSequences = freeAccountNumbersRepository.countFreeAccountNumberByIdType(type);
        if (amountSequences < MIN_NUMBER_OF_FREE_ACCOUNTS) {
            long incrementKey = accountNumbersSequenceRepository.incrementAndGet(type);
            String prefixCode = type.getCode();
            List<FreeAccountNumber> accountNumbers = IntStream
                    .rangeClosed(1, MAX_NUMBER_OF_FREE_ACCOUNTS)
                    .mapToObj(i -> concatenate(incrementKey + i, prefixCode))
                    .map(accountNumber -> {
                        FreeAccountNumberId id = new FreeAccountNumberId(type, accountNumber);
                        return new FreeAccountNumber(id);
                    })
                    .toList();
            freeAccountNumbersRepository.saveAll(accountNumbers);
            accountNumbersSequenceRepository.incrementToValueAndGet(MAX_NUMBER_OF_FREE_ACCOUNTS, type);
            log.info("Generated {} account numbers for type {}", MAX_NUMBER_OF_FREE_ACCOUNTS, type);
        }
    }


    @Transactional
    public Optional<String> getFreeAccountNumberByType(AccountNumberType type) {
        return freeAccountNumbersRepository.getFreeAccountNumberByType(type);
    }

    @Transactional
    public String generateAccountNumber(AccountNumberType type) {
        long incrementKey = accountNumbersSequenceRepository.incrementAndGet(type);
        String prefixCode = type.getCode();
        String accountNumber = concatenate(incrementKey, prefixCode);
        log.info("Generated account number {} for type {}", accountNumber, type);
        return accountNumber;
    }

    private String concatenate(long incrementKey, String prefixCode) {
        int keyLength = String.valueOf(incrementKey).length();
        int prefixLength = prefixCode.length();

        int zerosCount = MAX_DIGIT_OF_NUMBER - prefixLength - keyLength;
        if (zerosCount < 0) {
            throw new IllegalArgumentException("Total length exceeds maxLength limit.");
        }
        return prefixCode + "0".repeat(zerosCount) + incrementKey;
    }
}
