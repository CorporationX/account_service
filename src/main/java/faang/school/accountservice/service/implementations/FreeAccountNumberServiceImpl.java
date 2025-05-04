package faang.school.accountservice.service.implementations;

import faang.school.accountservice.entity.AccountSequence;
import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.GenerateNumberException;
import faang.school.accountservice.exception.free_account_numbers_exception.AccountNumberException;
import faang.school.accountservice.exception.free_account_numbers_exception.AccountNumberGenerationException;
import faang.school.accountservice.exception.free_account_numbers_exception.CounterIncrementException;
import faang.school.accountservice.exception.free_account_numbers_exception.CounterOverflowException;
import faang.school.accountservice.exception.free_account_numbers_exception.FreeAccountNumberNotFoundException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
import faang.school.accountservice.service.interfaces.FreeAccountNumberService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class FreeAccountNumberServiceImpl implements FreeAccountNumberService {
    private static final String FIXED_PREFIX = "1111222233334444";
    private static final int MAX_ATTEMPTS = 100;
    private static final AtomicInteger sequence = new AtomicInteger(0);
    private final AccountRepository accountRepository;

    private static final int MAX_RETRIES = 10;
    private static final int DEFAULT_BATCH_SIZE = 1;

    private final FreeAccountNumberRepository freeAccountNumberRepository;
    private final AccountNumbersSequenceRepository sequenceRepository;

    @Transactional
    @Override
    public void generateAccountNumbers(@NotNull AccountType type, int batchSize) {
        validateBatchSize(batchSize);

        initializeCounterIfNotExists(type);

        AccountSequence sequence = sequenceRepository.findById(type)
                .orElseThrow(() -> new AccountNumberException(
                        String.format("AccountSequence not found for type: %s", type)));

        long initialCounter = sequence.getCounter();
        long version = sequence.getVersion();
        long newCounter = initialCounter + batchSize;

        validateCounterOverflow(initialCounter, batchSize, type);

        boolean success = incrementCounterWithRetries(type, batchSize, initialCounter, version);

        List<FreeAccountNumber> numbers = generateFreeAccountNumbers(type, initialCounter, newCounter);

        try {
            freeAccountNumberRepository.saveAll(numbers);
        } catch (Exception e) {
            log.error("Error while saving free account numbers: type={}, numbers={}",
                    type, numbers, e);
            throw new AccountNumberGenerationException(
                    String.format("Failed to save free account numbers for type %s: %d numbers",
                            type, numbers.size()), e);
        }
    }

    @Override
    public void generateOneAccountNumber(@NotNull AccountType type) {
        generateAccountNumbers(type, DEFAULT_BATCH_SIZE);
    }

    @Transactional
    @Override
    public void useFreeAccountNumber(@NotNull AccountType type, Consumer<Long> action) {
        Objects.requireNonNull(action, "Action consumer must not be null");

        FreeAccountNumber freeAccountNumber = getAndRemoveFirstFreeAccountNumber(type);

        if (freeAccountNumber == null) {
            try {
                generateAccountNumbers(type, DEFAULT_BATCH_SIZE);
                freeAccountNumber = getAndRemoveFirstFreeAccountNumber(type);
            } catch (Exception e) {
                throw new AccountNumberGenerationException(
                        String.format("Failed to generate a new free account number for type %s", type), e);
            }

            if (freeAccountNumber == null) {
                throw new FreeAccountNumberNotFoundException(
                        String.format("Failed to retrieve a free account number for type %s after generation", type));
            }
        }

        long accountNumber = freeAccountNumber.getId().getAccountNumber();
        try {
            action.accept(accountNumber);
        } catch (Exception e) {
            throw new AccountNumberException(
                    String.format("Failed to execute action with account number %d for type %s",
                            accountNumber, type), e);
        }
    }

    private void validateBatchSize(int batchSize) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be positive: " + batchSize);
        }
    }

    private void initializeCounterIfNotExists(@NotNull AccountType type) {
        try {
            sequenceRepository.createCounter(type);
        } catch (Exception e) {
            throw new AccountNumberException(
                    String.format("Failed to initialize counter for type %s", type), e);
        }
    }

    private void validateCounterOverflow(long initialCounter, int batchSize, @NotNull AccountType type) {
        long maxLongValue = Long.MAX_VALUE;
        if (maxLongValue - batchSize < initialCounter) {
            throw new CounterOverflowException(
                    String.format("Counter overflow for type %s: initialCounter=%d, batchSize=%d, maxValue=%d",
                            type, initialCounter, batchSize, maxLongValue));
        }
    }

    private boolean incrementCounterWithRetries(@NotNull AccountType type,
                                                int batchSize,
                                                long initialCounter,
                                                long version) {
        boolean success = false;
        int attempts = 0;
        long currentCounter = initialCounter;
        long currentVersion = version;
        long newCounter = currentCounter + batchSize;

        while (!success && attempts < MAX_RETRIES) {
            success = sequenceRepository.incrementCounterIfMatch(type, batchSize, currentCounter, currentVersion);
            if (!success) {
                final int attempts_ = attempts++;
                AccountSequence sequence = sequenceRepository.findById(type)
                        .orElseThrow(() -> new AccountNumberException(
                                String.format("AccountSequence not found for type %s during retry attempt %d",
                                        type, attempts_)));
                currentCounter = sequence.getCounter();
                currentVersion = sequence.getVersion();
                newCounter = currentCounter + batchSize;
                validateCounterOverflow(currentCounter, batchSize, type);
            }
        }

        if (!success) {
            throw new CounterIncrementException(
                    String.format("Failed to increment counter for type %s after %d attempts: " +
                                    "initialCounter=%d, batchSize=%d",
                            type, MAX_RETRIES, initialCounter, batchSize));
        }

        return success;
    }

    private List<FreeAccountNumber> generateFreeAccountNumbers(@NotNull AccountType type,
                                                               long initialCounter,
                                                               long newCounter) {
        List<FreeAccountNumber> numbers = new ArrayList<>();
        for (long i = initialCounter + 1; i <= newCounter; i++) {
            long accountNumber = generateAccountNumber(type, i);
            FreeAccountId id = new FreeAccountId(type, accountNumber);
            if (freeAccountNumberRepository.existsById(id)) {
                log.warn("Skipping duplicate account number: type={}, accountNumber={}", type, accountNumber);
                continue;
            }
            numbers.add(new FreeAccountNumber(id));
        }
        return numbers;
    }

    private FreeAccountNumber getAndRemoveFirstFreeAccountNumber(@NonNull AccountType type) {
        List<Object[]> result = freeAccountNumberRepository.retrieveFirst(type.name());
        if (result.isEmpty()) {
            return null;
        }

        Object[] row = result.get(0);
        long accountNumber = ((Number) row[0]).longValue();
        String accountTypeStr = (String) row[1];

        FreeAccountId id = new FreeAccountId(AccountType.valueOf(accountTypeStr), accountNumber);
        return new FreeAccountNumber(id);
    }

    private long generateAccountNumber(@NonNull AccountType type, long counter) {
        String typePrefix = String.valueOf(type.getCode());
        String counterStr = String.format("%012d", counter);
        return Long.parseLong(typePrefix + counterStr);
    }

    @Override
    public String generateAccountNumber(AccountType accountType) {
        int attempts = 0;
        while (attempts < MAX_ATTEMPTS) {
            String lastFourDigits = String.format("%04d", sequence.getAndIncrement() % 10000);
            String accountNumber = FIXED_PREFIX + lastFourDigits;
            if (!accountRepository.existsByAccountNumber(accountNumber)) {
                log.info("Generated unique account number: {}", accountNumber);
                return accountNumber;
            }
            attempts++;
            log.debug("Account number {} already exists, retrying (attempt {}/{})",
                    accountNumber, attempts, MAX_ATTEMPTS);
        }
        throw new GenerateNumberException("Failed to generate unique account number after " +
                MAX_ATTEMPTS + " attempts");
    }
}
