package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.AccountNumberSequence;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.model.FreeAccountNumberId;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeAccountNumbersService {
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Transactional
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
                    + accountType + "current count = " + currentCount);
        }

        String uniqNumber = createUniqNumberByType(prefix, currentCount, length);
        FreeAccountNumber freeAccountNumber = FreeAccountNumber.builder()
                .id(
                        FreeAccountNumberId.builder()
                                .accountType(accountType)
                                .number(uniqNumber)
                                .build()
                )
                .build();

        return freeAccountNumbersRepository.save(freeAccountNumber);
    }
    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 5000L))
    public void getUniqueAccountNumberByType(Consumer<FreeAccountNumber> consumer, AccountType accountType) {

        FreeAccountNumber accountNumber = freeAccountNumbersRepository.getFirstAndDelete(accountType)
                .orElseGet(() -> generateNewAccountNumberByType(accountType));
        consumer.accept(accountNumber);
    }

    private String createUniqNumberByType(int prefixValue, long countValue, int length) {
        StringBuilder uniqNumber = new StringBuilder();
        String prefix = String.valueOf(prefixValue);
        String count = String.valueOf(countValue);

        int numberOfZeros = length - prefix.length() - count.length();
        if (numberOfZeros < 0) {
            throw new IllegalArgumentException("Current sequence count is greater than the maximum length");
        }
        uniqNumber.append(prefix);
        uniqNumber.append("0".repeat(numberOfZeros));
        uniqNumber.append(count);

        return uniqNumber.toString();
    }
}
