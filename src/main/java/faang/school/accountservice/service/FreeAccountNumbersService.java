package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumberSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeAccountNumbersService {
    private final int single = 1;
    private final long divisor = 1_000_000_000_000_000L;
    private final AccountNumberSequenceRepository accountNumberSequenceRepository;
    private final FreeAccountNumberRepository freeAccountNumberRepository;

    @Transactional
    public void processAndDeleteFreeAccNumber(AccountType type, Consumer<FreeAccountNumber> consumer) {
        FreeAccountNumber freeAccNum = deleteReturning(type);
        consumer.accept(freeAccNum);
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    public List<FreeAccountNumber> generateFreeAccountNumber(AccountType type, int batchSize) {
        AccountNumberSequence sequence = accountNumberSequenceRepository.createCounterForType(type);
        boolean incremented = incrementCounterIfMatches(type, batchSize, sequence.getCounter());
        if (!incremented) {
            throw new OptimisticLockException("Failed to increment account sequence due to concurrent modification");
        }
        List<FreeAccountNumber> numbers = new ArrayList<>();
        for (long i = sequence.getCounter(); i < sequence.getCounter() + batchSize; i++) {
            FreeAccountId freeAccountId = new FreeAccountId(type, generateNumber(type.getAccountTypePattern(), i));
            numbers.add(new FreeAccountNumber(freeAccountId));
        }
        return freeAccountNumberRepository.saveAll(numbers);
    }

    private boolean incrementCounterIfMatches(AccountType type, int batchSize, Long expectedCounter) {
        int updatedRows = accountNumberSequenceRepository.incrementCounterIfMatch(type, batchSize, expectedCounter);
        return updatedRows > 0;
    }

    @Transactional
    private FreeAccountNumber deleteReturning(AccountType type) {
        FreeAccountNumber freeAccNum = freeAccountNumberRepository.findFirstByIdType(type);
        if (freeAccNum == null) {
            freeAccNum = generateFreeAccountNumber(type, single).get(0);
        }
        freeAccountNumberRepository.delete(freeAccNum);
        return freeAccNum;
    }

    private long generateNumber(long pattern, long currentIteration) {
        long fixedPart = pattern / divisor;
        long remainderPart = pattern % divisor;
        long newRemainder = remainderPart + currentIteration;
        return fixedPart * divisor + newRemainder;
    }

    @Recover
    private void recoverFromOptimisticLockException(OptimisticLockException e, AccountType type) {
        log.error("Retries exhausted while incrementing counter of type: {}", type, e);
        throw e;
    }
}
