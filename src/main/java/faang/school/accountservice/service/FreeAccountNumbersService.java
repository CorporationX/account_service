package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.entity.FreeAccountNumbers;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.AccountNumberGenerationException;
import faang.school.accountservice.exception.InvalidAccountNumberException;
import faang.school.accountservice.exception.NoAvailableAccountNumberException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeAccountNumbersService {

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository sequenceRepository;

    private static final int MAX_RETRY_ATTEMPTS = 5;
    private static final int DEFAULT_BATCH_SIZE = 100;
    private static final int MAX_DUPLICATE_RETRIES = 3;

    @Transactional
    public FreeAccountNumbers createFreeAccountNumber(AccountType accountType, String accountNumber) {
        log.debug("Creating free account number {} for type {}", accountNumber, accountType);

        if (!accountType.isValidAccountNumber(accountNumber)) {
            throw new InvalidAccountNumberException(
                    String.format("Invalid account number '%s' for account type %s", accountNumber, accountType));
        }

        try {
            return freeAccountNumbersRepository.createFreeAccountNumber(accountType, accountNumber);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidAccountNumberException(
                    String.format("Account number '%s' already exists or violates constraints", accountNumber), e);
        }
    }

    @Transactional
    public List<FreeAccountNumbers> generateFreeAccountNumbers(AccountType accountType, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive: " + count);
        }

        log.info("Generating {} free account numbers for type {}", count, accountType);

        Long currentSequence = getCurrentSequence(accountType);
        if (!canGenerateNumbers(accountType, currentSequence, count)) {
            throw new AccountNumberGenerationException(
                    String.format("Cannot generate %d numbers for type %s: would exceed maximum sequence value",
                            count, accountType));
        }

        try {
            return generateNumbersWithDeduplication(accountType, count);
        } catch (Exception e) {
            log.error("Failed to generate account numbers for type {}: {}", accountType, e.getMessage());
            throw new AccountNumberGenerationException(
                    "Failed to generate " + count + " account numbers for type " + accountType, e);
        }
    }

    private List<FreeAccountNumbers> generateNumbersWithDeduplication(AccountType accountType, int count) {
        List<FreeAccountNumbers> generatedNumbers = new ArrayList<>();
        Set<String> existingNumbers = getExistingAccountNumbers(accountType);

        for (int attempt = 0; attempt < MAX_DUPLICATE_RETRIES && generatedNumbers.size() < count; attempt++) {
            try {
                int remainingCount = count - generatedNumbers.size();
                Long startSequence = sequenceRepository.reserveSequenceBlock(
                        accountType, remainingCount, MAX_RETRY_ATTEMPTS);

                List<FreeAccountNumbers> batchNumbers = IntStream.range(0, remainingCount)
                        .mapToObj(i -> {
                            Long sequence = startSequence + i;
                            String accountNumber = accountType.generateAccountNumber(sequence);
                            return new FreeAccountNumbers(accountType, accountNumber);
                        })
                        .filter(number -> !existingNumbers.contains(number.getAccountNumber()))
                        .toList();

                batchNumbers.forEach(number -> existingNumbers.add(number.getAccountNumber()));

                List<FreeAccountNumbers> savedNumbers = saveBatchWithDuplicateHandling(batchNumbers);
                generatedNumbers.addAll(savedNumbers);

                log.debug("Generated batch of {} numbers for type {} (attempt {})",
                        savedNumbers.size(), accountType, attempt + 1);

            } catch (Exception e) {
                log.warn("Attempt {} failed for type {}: {}", attempt + 1, accountType, e.getMessage());
                if (attempt == MAX_DUPLICATE_RETRIES - 1) {
                    throw e;
                }
            }
        }

        if (generatedNumbers.size() < count) {
            log.warn("Generated only {} out of {} requested numbers for type {}",
                    generatedNumbers.size(), count, accountType);
        }

        log.info("Successfully generated {} account numbers for type {}", generatedNumbers.size(), accountType);
        return generatedNumbers;
    }

    private Set<String> getExistingAccountNumbers(AccountType accountType) {
        try {
            return new HashSet<>(freeAccountNumbersRepository.findAll()
                    .stream()
                    .filter(number -> number.getAccountType() == accountType)
                    .map(FreeAccountNumbers::getAccountNumber)
                    .toList());
        } catch (Exception e) {
            log.warn("Failed to load existing numbers for type {}, proceeding without deduplication: {}",
                    accountType, e.getMessage());
            return new HashSet<>();
        }
    }

    private List<FreeAccountNumbers> saveBatchWithDuplicateHandling(List<FreeAccountNumbers> numbers) {
        try {
            return freeAccountNumbersRepository.saveAll(numbers);
        } catch (DataIntegrityViolationException e) {
            log.debug("Batch save failed, trying individual saves: {}", e.getMessage());
            return saveIndividually(numbers);
        }
    }

    private List<FreeAccountNumbers> saveIndividually(List<FreeAccountNumbers> numbers) {
        List<FreeAccountNumbers> savedNumbers = new ArrayList<>();

        for (FreeAccountNumbers number : numbers) {
            try {
                FreeAccountNumbers saved = freeAccountNumbersRepository.save(number);
                savedNumbers.add(saved);
            } catch (DataIntegrityViolationException e) {
                log.debug("Skipping duplicate number: {}", number.getAccountNumber());
            }
        }

        return savedNumbers;
    }

    private boolean canGenerateNumbers(AccountType accountType, Long currentSequence, int count) {
        long maxSequence = accountType.getMaxSequenceValue();
        return currentSequence + count <= maxSequence;
    }

    @Transactional
    public <T> T executeWithFreeAccountNumber(AccountType accountType, Function<String, T> operation) {
        String accountNumber = getAndReserveFreeAccountNumber(accountType);

        try {
            T result = operation.apply(accountNumber);
            log.debug("Successfully executed operation with account number {} for type {}",
                    accountNumber, accountType);
            return result;
        } catch (Exception e) {
            log.error("Failed to execute operation with account number {} for type {}: {}",
                    accountNumber, accountType, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public String getAndReserveFreeAccountNumber(AccountType accountType) {
        Optional<String> freeNumber = freeAccountNumbersRepository
                .findAndDeleteFirstAvailableNumber(accountType.name());

        if (freeNumber.isPresent()) {
            String accountNumber = freeNumber.get();
            if (!accountType.isValidAccountNumber(accountNumber)) {
                log.warn("Retrieved invalid account number {} for type {}, generating new one",
                        accountNumber, accountType);
                return generateNewAccountNumberDirectly(accountType);
            }

            log.debug("Retrieved free account number for type {}", accountType);
            return accountNumber;
        }

        log.info("No free account numbers found for type {}, generating new one", accountType);
        return generateNewAccountNumberDirectly(accountType);
    }

    private String generateNewAccountNumberDirectly(AccountType accountType) {
        try {
            Long sequence = sequenceRepository.getNextSequenceValue(accountType, MAX_RETRY_ATTEMPTS);

            if (!accountType.isSequenceValid(sequence)) {
                throw new NoAvailableAccountNumberException(
                        String.format("Sequence %d exceeds maximum value for account type %s",
                                sequence, accountType));
            }

            String accountNumber = accountType.generateAccountNumber(sequence);
            log.debug("Generated new account number for type {}", accountType);
            return accountNumber;

        } catch (AccountNumberGenerationException e) {
            throw new NoAvailableAccountNumberException(
                    "Unable to generate new account number for type " + accountType +
                            " after " + MAX_RETRY_ATTEMPTS + " attempts", e);
        } catch (Exception e) {
            log.error("Unexpected error while generating account number for type {}: {}",
                    accountType, e.getMessage());
            throw new NoAvailableAccountNumberException(
                    "Unexpected error while generating account number for type " + accountType, e);
        }
    }

    @Transactional(readOnly = true)
    public long countFreeAccountNumbers(AccountType accountType) {
        return freeAccountNumbersRepository.countByAccountType(accountType);
    }

    @Transactional(readOnly = true)
    public boolean hasFreeAccountNumbers(AccountType accountType) {
        return freeAccountNumbersRepository.existsByAccountType(accountType);
    }

    @Transactional(readOnly = true)
    public Long getCurrentSequence(AccountType accountType) {
        return sequenceRepository.findById(accountType)
                .map(AccountNumbersSequence::getCurrentSequence)
                .orElse(0L);
    }

    @Transactional
    public AccountNumbersSequence initializeSequence(AccountType accountType) {
        log.info("Initializing sequence for account type {}", accountType);
        return sequenceRepository.createSequenceForType(accountType);
    }

    @Transactional
    public int ensureMinimumFreeNumbers(AccountType accountType, int minCount) {
        if (minCount <= 0) {
            throw new IllegalArgumentException("Minimum count must be positive: " + minCount);
        }

        long currentCount = countFreeAccountNumbers(accountType);

        if (currentCount >= minCount) {
            log.debug("Sufficient free numbers for type {}: {} >= {}",
                    accountType, currentCount, minCount);
            return 0;
        }

        int numberToGenerate = (int) (minCount - currentCount);
        log.info("Generating {} additional free numbers for type {} (current: {}, required: {})",
                numberToGenerate, accountType, currentCount, minCount);

        try {
            generateFreeAccountNumbers(accountType, numberToGenerate);
            return numberToGenerate;
        } catch (Exception e) {
            log.error("Failed to ensure minimum free numbers for type {}: {}", accountType, e.getMessage());
            throw new AccountNumberGenerationException(
                    "Failed to ensure minimum free numbers for type " + accountType, e);
        }
    }

    @Transactional
    public void generateFreeNumbersForAllTypes(int countPerType) {
        if (countPerType <= 0) {
            throw new IllegalArgumentException("Count per type must be positive: " + countPerType);
        }

        log.info("Generating {} free numbers for all account types", countPerType);

        for (AccountType accountType : AccountType.values()) {
            try {
                generateFreeAccountNumbers(accountType, countPerType);
                log.debug("Generated {} numbers for type {}", countPerType, accountType);
            } catch (Exception e) {
                log.error("Failed to generate numbers for type {}: {}", accountType, e.getMessage());
            }
        }
    }

    public void validateAccountNumber(AccountType accountType, String accountNumber) {
        if (!accountType.isValidAccountNumber(accountNumber)) {
            throw new InvalidAccountNumberException(
                    String.format("Invalid account number '%s' for account type %s. " +
                                    "Expected format: %s + 8-16 digits (total length 12-20 characters)",
                            accountNumber, accountType, accountType.getPrefix()));
        }
    }
}