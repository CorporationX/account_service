package faang.school.accountservice.service;

import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {

    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Value("${account.number.length}")
    private int numberLength;

    @Transactional
    public void generateAccountNumbersUpTo(long upToSize, AccountType accountType) {
        log.info("Create new free numbers for Account Type: {} up to {}", accountType.name(), upToSize);
        long newCounter = accountNumbersSequenceRepository.getByType(accountType.name()).getCounter();
        long batchSize = upToSize - newCounter;
        if (batchSize > 0) {
            generateAccountNumbers((int) batchSize, accountType);
        } else {
            log.info("Number of accounts is already equal to or greater than required: {}",  upToSize);
        }
    }

    @Transactional
    public void generateAccountNumbers(int batchSize, AccountType accountType) {
        log.info("Create new {} free numbers for Account Type: {}", batchSize, accountType.name());
        long newCounter = accountNumbersSequenceRepository.getByType(accountType.name()).getCounter();
        List<FreeAccountNumber> numbers = new ArrayList<>();
        for (int i = 0; i < batchSize; i++, newCounter++) {
            numbers.add(FreeAccountNumber.builder()
                    .number(accountType.getPattern() + newCounter)
                    .type(accountType)
                    .build());
        }
        freeAccountNumbersRepository.saveAll(numbers);
        accountNumbersSequenceRepository.incrementCounter(accountType.name(), batchSize);
    }

    @Transactional
    public void createNewFreeNumber(AccountType accountType) {
        log.info("Create new free number for Account Type: {}", accountType.name());
        long newCounter = accountNumbersSequenceRepository.getByType(accountType.name()).getCounter();
        FreeAccountNumber freeAccountNumber = FreeAccountNumber.builder()
                .number(accountType.getPattern() + newCounter)
                .type(accountType)
                .build();
        freeAccountNumbersRepository.save(freeAccountNumber);
        accountNumbersSequenceRepository.incrementCounter(accountType.name(), 1);
    }

    @Transactional
    public String getFreeNumber(AccountType accountType) {
        FreeAccountNumber number = freeAccountNumbersRepository.getAndDeleteFirst(accountType.name());
        if (number == null) {
            log.info("Not found free numbers for Account Type: {}", accountType.name());
            createNewFreeNumber(accountType);
            number = freeAccountNumbersRepository.getAndDeleteFirst(accountType.name());
        }
        return String.valueOf(number.getNumber());
    }
}
