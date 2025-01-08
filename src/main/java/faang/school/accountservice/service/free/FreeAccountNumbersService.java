package faang.school.accountservice.service.free;

import faang.school.accountservice.entity.account.AccountNumbersSequence;
import faang.school.accountservice.entity.free.FreeAccountId;
import faang.school.accountservice.entity.free.FreeAccountNumber;
import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.repository.account.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.free.FreeAccountNumbersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {

    private static final long ACCOUNT_PATTERN = 4200_0000_0000_0000L;
    private static final long MAX_ACCOUNT_NUMBERS = 9999_9999_9999L;

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Transactional
    public void generateAccountNumbers(AccountType type, int batchSize) {
        validateBatchSize(batchSize);
        AccountNumbersSequence period = incrementAndGetCounter(type.name(), batchSize);
        long initialValue = period.getInitialValue();
        long counter = period.getCounter();
        validateCounter(counter);
        List<FreeAccountNumber> accountNumbers = new ArrayList<>();
        log.info("Generating {} new account numbers for type: {}", batchSize, type);
        for (long i = initialValue; i < counter; i++) {
            accountNumbers.add(new FreeAccountNumber(
                    new FreeAccountId(type, ACCOUNT_PATTERN + i)));
        }
        freeAccountNumbersRepository.saveAll(accountNumbers);
    }

    @Transactional
    public void ensureAccountNumbers(AccountType type, int targetCount) {
        validateTargetCount(targetCount);
        long currentCount = freeAccountNumbersRepository.countByType(type.name());

        if (currentCount < targetCount) {
            int batchSize = (int) (targetCount - currentCount);
            generateAccountNumbers(type, batchSize);
        } else {
            log.info("Sufficient account numbers already exist for type: {}, no new accounts generated.", type);
        }
    }

    @Transactional
    public String getFreeAccountNumber(AccountType type) {
        return freeAccountNumbersRepository
                .getFreeAccountNumber(type.name())
                .orElseThrow(() -> new IllegalArgumentException("No free account number found by type: " + type));
    }

    @Transactional
    public void retrieveAccountNumber(AccountType type, Consumer<FreeAccountNumber> numberConsumer) {
        numberConsumer.accept(freeAccountNumbersRepository.retrieveFirst(type.name()));
    }

    public AccountNumbersSequence incrementAndGetCounter(String type, int batchSize) {
        return accountNumbersSequenceRepository.incrementAndGetCounter(type, batchSize);
    }

    public void saveAllAccountNumbers(List<FreeAccountNumber> numbers) {
        freeAccountNumbersRepository.saveAll(numbers);
    }

    public void validateBatchSize(int batchSize) {
        if (batchSize <=0){
            log.error("batchSize must be greater than 0");
            throw new IllegalArgumentException("batchSize must be greater than 0");
        }
    }

    public void validateCounter(long counter) {
        if (counter > MAX_ACCOUNT_NUMBERS){
            log.error("Reached the limit of accounts numbers: {}", MAX_ACCOUNT_NUMBERS);
            throw new IllegalArgumentException("Reached the limit of accounts numbers: " +MAX_ACCOUNT_NUMBERS);
        }
    }

    public void validateTargetCount(long targetCount) {
        if (targetCount > MAX_ACCOUNT_NUMBERS){
            log.error("Reached the limit of accounts numbers: {}", MAX_ACCOUNT_NUMBERS);
            throw new IllegalArgumentException("Reached the limit of accounts numbers: " +MAX_ACCOUNT_NUMBERS);
        }
    }

}
