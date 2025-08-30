package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.account.AccountSequence;
import faang.school.accountservice.entity.account.FreeAccountId;
import faang.school.accountservice.entity.account.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.service.account.number.AccountNumberGenerator;
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
public class FreeAccountNumberServiceImpl implements FreeAccountNumberService {

    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    private static final int MIN_BATCH_SIZE = 0;

    @Transactional
    @Override
    public void generateAccountNumbers(AccountType accountType, int batchSize) {
        AccountSequence period = accountNumbersSequenceRepository.incrementCounter(accountType.name(), batchSize);
        long start = period.getInitialValue();
        long end = period.getCounter();
        List<FreeAccountNumber> numbers = new ArrayList<>();
        for (long i = start; i < end; i++) {
            long accountNumber = accountNumberGenerator.generate(accountType, i);
            numbers.add(new FreeAccountNumber(new FreeAccountId(accountType, accountNumber)));
        }
        freeAccountNumbersRepository.saveAll(numbers);
        log.info("Generated {} new account numbers for type {}", numbers.size(), accountType);
    }

    @Transactional
    @Override
    public void retrieveAccountNumbers(AccountType type, int batchSize, Consumer<List<FreeAccountNumber>> consumer) {
        List<FreeAccountNumber> retrievedNumbers = new ArrayList<>(batchSize);
        retrievedNumbers.addAll(freeAccountNumbersRepository.retrieveNumbers(type.name(), batchSize));
        int remainingToGenerate = batchSize - retrievedNumbers.size();
        if (remainingToGenerate > MIN_BATCH_SIZE) {
            AccountSequence sequence = accountNumbersSequenceRepository.incrementCounter(type.name(), remainingToGenerate);
            long startValue = sequence.getInitialValue();
            long endValue = sequence.getCounter();
            for (long currentValue = startValue; currentValue < endValue; currentValue++) {
                long generatedNumber = accountNumberGenerator.generate(type, currentValue);
                retrievedNumbers.add(new FreeAccountNumber(new FreeAccountId(type, generatedNumber)));
            }
            log.info("Generated {} new account numbers for type {}", remainingToGenerate, type);
        } else {
            log.debug("Retrieved {} free account numbers for type {}", retrievedNumbers.size(), type);
        }
        consumer.accept(retrievedNumbers);
    }
}
