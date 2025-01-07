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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {
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
        AccountNumberSequence accNumSeq = accountNumberSequenceRepository.findByType(type).orElseThrow(
                () -> new IllegalArgumentException("Sequence for account type not found"));
        boolean incremented = incrementCounterIfMatches(type, batchSize, accNumSeq.getCounter());
        if (!incremented) {
            throw new OptimisticLockException("Failed to increment account sequence due to concurrent modification");
        }
        List<FreeAccountNumber> numbers = new ArrayList<>();
        for (long i = accNumSeq.getCounter(); i < accNumSeq.getCounter() + batchSize; i++) {
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
            freeAccNum = generateFreeAccountNumber(type, 1).get(0);
        }
        freeAccountNumberRepository.delete(freeAccNum);
        return freeAccNum;
    }

    private long generateNumber(long pattern, long currentIteration) {
        long divisor = 1_000_000_000_000_000L;
        long fixedPart = pattern / divisor;
        long remainderPart = pattern % divisor;
        long newRemainder = remainderPart + currentIteration;
        return fixedPart * divisor + newRemainder;
    }
}
