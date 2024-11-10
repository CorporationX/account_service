package faang.school.accountservice.service.account.numbers.sequence;

import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.model.number.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.service.account.numbers.AccountNumberConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
class DigitSequenceGenerator implements IDigitSequenceGenerator{
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumberConfig accountNumberConfig;

    @Transactional
    public Optional<String> getAndRemoveFreeAccountNumberByType(AccountNumberType type) {
        Optional<String> accountNumber = freeAccountNumbersRepository.getAndRemoveFromPoolFreeAccountNumberByType(type.toString());
        if (accountNumber.isPresent()) {
            log.info("From pool Get and Removed account number {} for type {}", accountNumber.get(), type);
        } else {
            log.warn("Unable to remove account number");
        }
        return accountNumber;
    }

    @Transactional
    public String generateNewAccountNumberWithoutPool(AccountNumberType type) {
        accountNumbersSequenceRepository.tryLockCounterByTypeForUpdate(type.toString());
        long incrementKey = accountNumbersSequenceRepository.incrementAndGet(type.toString());
        String prefixCode = type.getCode();
        String accountNumber = concatenate(incrementKey, prefixCode);
        log.info("Generated new account number pass pull {} for type {}", accountNumber, type);
        return accountNumber;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generationSequencesAsync(AccountNumberType type) {
        generateAndSaveAccountNumbers(type);
        accountNumbersSequenceRepository.setNonActiveGenerationState(type.toString());
    }

    public boolean isGenerationNeeded(AccountNumberType type) {
        long amountSequences = countExistingFreeAccountNumbers(type);
        return amountSequences < accountNumberConfig.getPoolDrawFailureExhaustionWarningUpperLimit();

    }


    private void generateAndSaveAccountNumbers(AccountNumberType type) {
        List<FreeAccountNumber> accountNumbers = generateAccountNumbers(type);
        List<String> saved = saveGeneratedAccountNumbers(accountNumbers);
        updateAccountNumberCounter(type);
        log.info("Generated {} account numbers for type {} -> {}", saved.size(), type, saved);

    }


    private long countExistingFreeAccountNumbers(AccountNumberType type) {
        return freeAccountNumbersRepository.countFreeAccountNumberByIdType(type);
    }

    private List<FreeAccountNumber> generateAccountNumbers(AccountNumberType type) {
        return generationSequencesByNumberConfig(type);
    }

    private List<String> saveGeneratedAccountNumbers(List<FreeAccountNumber> accountNumbers) {
        return freeAccountNumbersRepository.saveAll(accountNumbers)
                .stream()
                .map(FreeAccountNumber::getDigitSequence)
                .toList();
    }

    private void updateAccountNumberCounter(AccountNumberType type) {
        accountNumbersSequenceRepository.upCounterAndGet(type.toString(), accountNumberConfig.getBatchSize() - 1);
    }

    private List<FreeAccountNumber> generationSequencesByNumberConfig(AccountNumberType type) {
        long incrementKey = accountNumbersSequenceRepository.incrementAndGet(type.toString());
        String prefixCode = type.getCode();
        return IntStream
                .range(0, accountNumberConfig.getBatchSize())
                .mapToObj(i -> concatenate(incrementKey + i, prefixCode))
                .map(accountNumber -> new FreeAccountNumber(type, accountNumber))
                .toList();
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
