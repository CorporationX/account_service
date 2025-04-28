package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountSeq;
import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.AccountNumberGenerationException;
import faang.school.accountservice.exception.AccountNumberNotFoundException;
import faang.school.accountservice.exception.InvalidBatchSizeException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeAccountNumbersService {
    private static final long ACCOUNT_PATTERN = 4200_0000_0000_0000L;

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Transactional
    public void generatedAccountNumbers(AccountType type, int batchSize) {
        if (batchSize <= 0) {
            log.error("Batch size must be positive");
            throw new InvalidBatchSizeException("Batch size must be positive");
        }

        AccountSeq period;
        try {
            period = accountNumbersSequenceRepository.incrementCounter(type.name(), batchSize);
        } catch (DataAccessException e) {
            log.error("Error incrementing counter for account type: {}", type, e);
            throw new AccountNumberGenerationException("Error generating account numbers for type: " + type, e);
        }

        List<FreeAccountNumber> numberList =
                LongStream.range(period.getInitialValue(), period.getCounter())
                        .mapToObj(i -> new FreeAccountNumber(new FreeAccountId(type,
                                ACCOUNT_PATTERN + i)))
                        .collect(Collectors.toList());

        log.info("Generating {} account numbers for type: {}", numberList.size(), type);
        freeAccountNumbersRepository.saveAll(numberList);
        log.info("Successfully generated {} account numbers for type: {}", numberList.size(), type);
    }

    @Transactional
    public void retrieveAccountNumber(AccountType accountType, Consumer<FreeAccountNumber> numberConsumer) {
        FreeAccountNumber accountNumber;
        try {
            accountNumber = freeAccountNumbersRepository.retrieveFirst(accountType.name());
        } catch (DataAccessException e) {
            log.error("Error retrieving account number for type: {}", accountType, e);
            throw new AccountNumberNotFoundException("Error retrieving account number for type: " + accountType);
        }

        if (accountNumber != null) {
            numberConsumer.accept(accountNumber);
            log.info("Retrieved account number for type: {}", accountType);
        } else {
            log.warn("No account number found for type: {}", accountType);
            throw new AccountNumberNotFoundException("No account number found for type: " + accountType);
        }
    }
}