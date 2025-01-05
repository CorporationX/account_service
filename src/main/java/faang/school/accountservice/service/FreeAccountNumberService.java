package faang.school.accountservice.service;


import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.model.AccountNumberSequence;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumberSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class FreeAccountNumberService {

    private final AccountNumberSequenceRepository accountNumberSequenceRepository;
    private final FreeAccountNumberRepository freeAccountNumberRepository;

    @Transactional
    public void processFreeAccountNumber(AccountNumberType accountNumberType, Consumer<FreeAccountNumber> consumer) {
        log.info("Trying to get a free account number of type: {}", accountNumberType);
        createAccountNumberIfNotExists(accountNumberType);
        FreeAccountNumber freeAccountNumber = freeAccountNumberRepository
                .getNextAccountNumberByType(accountNumberType.toString());
        freeAccountNumberRepository.delete(freeAccountNumber);
        consumer.accept(freeAccountNumber);
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 5, backoff = @Backoff(multiplier = 2.0))
    public void createAccountNumber(AccountNumberType accountNumberType) {
        log.info("Trying to create a new account number of type: {}", accountNumberType);
        AccountNumberSequence accountNumberSequence = accountNumberSequenceRepository.findByType(accountNumberType);
        String accountNumber = createAccountNumber(accountNumberType.prefix, accountNumberSequence.getCurrent());
        FreeAccountNumber freeAccountNumber = createFreeAccountNumber(accountNumberType, accountNumber);
        freeAccountNumberRepository.save(freeAccountNumber);
        accountNumberSequence.increment();
    }

    private void createAccountNumberIfNotExists(AccountNumberType accountNumberType) {
        boolean existsByType = freeAccountNumberRepository.existsByType(accountNumberType);
        if (!existsByType) {
            createAccountNumber(accountNumberType);
        }
    }

    private String createAccountNumber(long prefix, long currentCount) {
        return Long.toString((prefix * (long) Math.pow(10, 12) + currentCount));
    }

    private FreeAccountNumber createFreeAccountNumber(AccountNumberType accountNumberType, String accountNumber) {
        return FreeAccountNumber.builder()
                .type(accountNumberType)
                .accountNumber(accountNumber)
                .build();
    }
}
