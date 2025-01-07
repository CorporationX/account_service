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

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {
    public static final long CREDIT_PATTERN = 5236_000_000_000_000L;
    public static final long DEBIT_PATTERN = 4200_000_000_000_000L;
    private static final long ACCOUNT_NUMBER_DIVISOR = 1_000_000_000_000_000L;
    private final AccountNumberSequenceRepository accountNumberSequenceRepository;
    private final FreeAccountNumberRepository freeAccountNumberRepository;
    private final AccountService accountService;

    @Transactional
    public FreeAccountNumber deleteReturning(AccountType type) {
        FreeAccountNumber freeAccNum = freeAccountNumberRepository.findFirstByIdType(type).orElseThrow(
                () -> new IllegalArgumentException("Free Account Number for that type not found"));
        freeAccountNumberRepository.delete(freeAccNum);
        return freeAccNum;
    }

    @Transactional
    public void createAccountWithFreeNumber(AccountType type) {
        FreeAccountNumber freeAccNum = deleteReturning(type);
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void generateFreeAccountNumber(AccountType type, int batchSize, long pattern) {
        AccountNumberSequence accNumSeq = accountNumberSequenceRepository.findByType(type).orElseThrow(
                () -> new IllegalArgumentException("Sequence for account type not found"));
        boolean incremented = incrementCounterIfMatches(type, batchSize, accNumSeq.getCounter());
        if (!incremented) {
            throw new OptimisticLockException("Failed to increment account sequence due to concurrent modification");
        }
        List<FreeAccountNumber> numbers = new ArrayList<>();
        for (long i = accNumSeq.getCounter(); i < accNumSeq.getCounter() + batchSize; i++) {
            FreeAccountId freeAccountId = new FreeAccountId(type, generateNumber(pattern, i));
            numbers.add(new FreeAccountNumber(freeAccountId));
        }
        freeAccountNumberRepository.saveAll(numbers);
    }

    @Transactional
    public boolean incrementCounterIfMatches(AccountType type, int batchSize, Long expectedCounter) {
        int updatedRows = accountNumberSequenceRepository.incrementCounterIfMatch(type, batchSize, expectedCounter);
        return updatedRows > 0;
    }

    private long generateNumber(long pattern, long currentIteration) {
        long fixedPart = pattern / ACCOUNT_NUMBER_DIVISOR;
        long remainderPart = pattern % ACCOUNT_NUMBER_DIVISOR;
        long newRemainder = remainderPart + currentIteration;
        return fixedPart * ACCOUNT_NUMBER_DIVISOR + newRemainder;
    }
}
