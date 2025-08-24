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

    private static final int SINGLE_NUMBER_BATCH = 1;

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
    public void retrieveAccountNumber(AccountType accountType, Consumer<FreeAccountNumber> consumer) {
        FreeAccountNumber number = freeAccountNumbersRepository.retrieveFirst(accountType.name());
        if (number == null) {
            AccountSequence sequence = accountNumbersSequenceRepository.incrementCounter(accountType.name(), SINGLE_NUMBER_BATCH);
            long generated = accountNumberGenerator.generate(accountType, sequence.getInitialValue());
            number = new FreeAccountNumber(new FreeAccountId(accountType, generated));
            log.info("Generated account number {} for type {}", generated, accountType);
        } else {
            log.debug("Retrieved free account number {} for type {}", number.getId().getAccountNumber(), accountType);
        }
        consumer.accept(number);
    }
}
