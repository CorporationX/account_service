package faang.school.accountservice.service.account.numbers;

import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.model.number.FreeAccountNumber;
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
    private final AccountNumberConfig accountNumberConfig;


    @Async
    @Transactional
    public void checkForGenerationSequencesAsync(AccountNumberType type) {
        long amountSequences = freeAccountNumbersRepository.countFreeAccountNumberByType(type);
        if (amountSequences < accountNumberConfig.getMinNumberOfFreeAccounts()) {
            long incrementKey = accountNumbersSequenceRepository.incrementAndGet(type.toString());
            String prefixCode = type.getCode();
            List<FreeAccountNumber> accountNumbers = IntStream
                    .range(0, accountNumberConfig.getMaxNumberOfFreeAccounts())
                    .mapToObj(i -> concatenate(incrementKey + i, prefixCode))
                    .map(accountNumber -> new FreeAccountNumber(type, accountNumber))
                    .toList();
            freeAccountNumbersRepository.saveAll(accountNumbers);
            accountNumbersSequenceRepository.upCounterAndGet(type.toString(), accountNumberConfig.getMaxNumberOfFreeAccounts() - 1);

            log.info("Generated {} account numbers for type {}\n{}", accountNumberConfig.getMaxNumberOfFreeAccounts(), type, accountNumbers);
        }
    }


    @Transactional
    public Optional<String> getAndRemoveFreeAccountNumberByType(AccountNumberType type) {
        Optional<String> accountNumberOpt = freeAccountNumbersRepository.getFreeAccountNumberByType(type);

        return accountNumberOpt.map(digitSequence -> {
            log.info("Found free account number {} for type {}", digitSequence, type);

            if (freeAccountNumbersRepository.removeFreeAccountNumber(digitSequence) == 1) {
                log.info("Removed free account number {} for type {}", digitSequence, type);
                return digitSequence;
            } else {
                log.error("Failed to remove account number {}", digitSequence);
                return null;
            }
        });
    }


    @Transactional
    public String generateAccountNumber(AccountNumberType type) {
        long incrementKey = accountNumbersSequenceRepository.incrementAndGet(type.toString());
        String prefixCode = type.getCode();
        String accountNumber = concatenate(incrementKey, prefixCode);
        log.info("Generated new account number pass pull {} for type {}", accountNumber, type);
        return accountNumber;
    }

    private String concatenate(long incrementKey, String prefixCode) {
        int keyLength = String.valueOf(incrementKey).length();
        int prefixLength = prefixCode.length();

        int zerosCount = accountNumberConfig.getMaxlengthOfDigitSequence() - prefixLength - keyLength;
        if (zerosCount < 0) {
            throw new IllegalArgumentException("Total length exceeds maxLength limit.");
        }
        return prefixCode + "0".repeat(zerosCount) + incrementKey;
    }
}
