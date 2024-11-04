package faang.school.accountservice.service.account.numbers;

import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.model.number.AccountUniqueNumberCounter;
import faang.school.accountservice.model.number.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DigitSequenceService {
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumberConfig accountNumberConfig;


    @Async
    @Transactional
    public void checkForGenerationSequencesAsync(AccountNumberType type) {
        Optional<AccountUniqueNumberCounter> counterOpt =
                accountNumbersSequenceRepository.findByTypeForUpdate(type.toString());
        if (counterOpt.isEmpty()) {
            log.warn("No counter found for type: {}", type);
            return;
        }
        tryActivateGenerationState(type, counterOpt.get());
    }

    @Transactional
    public void tryActivateGenerationState(AccountNumberType type, AccountUniqueNumberCounter counter) {
        if (!counter.getGeneration_state()) {
            accountNumbersSequenceRepository.setActiveGenerationState(type.toString());
            generateAndSaveAccountNumbers(type);
            accountNumbersSequenceRepository.setNonActiveGenerationState(type.toString());
        } else {
            log.info("Generation is already in progress for type {}", type);
        }
    }

    @Transactional
    public void generateAndSaveAccountNumbers(AccountNumberType type) {
        long amountSequences = freeAccountNumbersRepository.countFreeAccountNumberByType(type);
        if (amountSequences < accountNumberConfig.getMinNumberOfFreeAccounts()) {
            List<FreeAccountNumber> accountNumbers = generationSequencesByNumberConfig(type);
            freeAccountNumbersRepository.saveAll(accountNumbers);
            accountNumbersSequenceRepository.upCounterAndGet(type.toString(), accountNumberConfig.getMaxNumberOfFreeAccounts() - 1);
            log.info("Saved {} account numbers for type {}\n{}", accountNumberConfig.getMaxNumberOfFreeAccounts(), type, accountNumbers);
        }
    }


    @Transactional
    public Optional<String> getAndRemoveFreeAccountNumberByType(AccountNumberType type) {
        Optional<String> accountNumber = freeAccountNumbersRepository.getFreeAccountNumberByType(type);
        if (accountNumber.isPresent()) {
            int deletedCount = freeAccountNumbersRepository.removeFreeAccountNumber(accountNumber.get());
            log.debug("From pool Get account number {} for type {}", accountNumber.get(), type);
            if (deletedCount == 1) {
                log.info("From pool Removed account number {} for type {}", accountNumber.get(), type);
            } else {
                log.warn("Unable to remove account number due to a version conflict.");
            }
        }
        return accountNumber;
    }


    @Transactional
    public String generateNewAccountNumberWithoutPool(AccountNumberType type) {
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

    private List<FreeAccountNumber> generationSequencesByNumberConfig(AccountNumberType type) {
        long incrementKey = accountNumbersSequenceRepository.incrementAndGet(type.toString());
        String prefixCode = type.getCode();
        return IntStream
                .range(0, accountNumberConfig.getMaxNumberOfFreeAccounts())
                .mapToObj(i -> concatenate(incrementKey + i, prefixCode))
                .map(accountNumber -> new FreeAccountNumber(type, accountNumber))
                .toList();
    }
}
