package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.AccountNumberSequence;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.model.FreeAccountNumberId;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigInteger;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeAccountNumbersService {
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 5000L))
    public FreeAccountNumber generateNewAccountNumberByType(AccountType accountType) {
        log.info("Starting generate new account, type = {}", accountType.toString());
        int prefix = accountType.getPrefix();
        int length = accountType.getLength();

        AccountNumberSequence accountNumberSequence = accountNumbersSequenceRepository.findByAccountType(accountType)
                .orElseGet(() -> accountNumbersSequenceRepository.createSequenceIfNecessary(accountType));

        long currentCount = accountNumberSequence.getCount();
        if (!accountNumbersSequenceRepository.isIncremented(currentCount, accountType)) {
            throw new OptimisticLockingFailureException("Couldn't increment count of account type: "
                    + accountType + " current count = " + currentCount);
        }

        BigInteger uniqNumber = createUniqNumberByType(prefix, currentCount, length);
        FreeAccountNumber freeAccountNumber = FreeAccountNumber.builder()
                .id(
                        FreeAccountNumberId.builder()
                                .accountType(accountType)
                                .number(uniqNumber)
                                .build()
                )
                .build();
        return freeAccountNumbersRepository.saveAndFlush(freeAccountNumber);
    }
    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 5000L))
    public void getUniqueAccountNumberByType(Consumer<FreeAccountNumber> consumer, AccountType accountType) {

        FreeAccountNumber accountNumber = freeAccountNumbersRepository.getFirstAndDelete(accountType)
                .orElseGet(() -> generateNewAccountNumberByType(accountType));
        consumer.accept(accountNumber);
    }

    private BigInteger createUniqNumberByType(int prefixValue, long countValue, int length) {
        BigInteger uniqNumber = BigInteger.valueOf(prefixValue);
        String prefix = String.valueOf(prefixValue);
        String count = String.valueOf(countValue);

        int numberOfZeros = length - prefix.length();
        if (numberOfZeros < count.length()) {
            throw new IllegalArgumentException("Current sequence count is greater than the maximum length");
        }
        uniqNumber = uniqNumber.multiply(BigInteger.valueOf(10).pow(numberOfZeros));
        uniqNumber = uniqNumber.add(BigInteger.valueOf(countValue));

        return uniqNumber;
    }
}
