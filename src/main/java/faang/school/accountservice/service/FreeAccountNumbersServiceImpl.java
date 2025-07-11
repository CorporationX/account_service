package faang.school.accountservice.service;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumbersServiceImpl implements FreeAccountNumbersService {
    private static final long ACCOUNT_PATTERN = 4200_0000_0000_0000L;

    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Transactional
    public void generateAccountNumbers(AccountType type, int batchSize) {
        Long startPeriodIndex = accountNumbersSequenceRepository.getCurrentCounterByType(type);
        accountNumbersSequenceRepository.incrementCounter(type, batchSize);
        Long endPeriodIndex = accountNumbersSequenceRepository.getCurrentCounterByType(type);
        List<FreeAccountNumber> generatedAccountNumbers = new ArrayList<>(batchSize);
        for (long i = startPeriodIndex; i < endPeriodIndex; i++) {
            generatedAccountNumbers.add(new FreeAccountNumber(
                    new FreeAccountNumberId(type, ACCOUNT_PATTERN + i)));
        }
        freeAccountNumbersRepository.saveAll(generatedAccountNumbers);
    }

    @Transactional
    public void acceptAccountNumber(AccountType type, Consumer<Long> accountNumberConsumer) {
        accountNumberConsumer.accept(retrieveAccountNumber(type));
    }

    @Transactional
    public Long retrieveAccountNumber(AccountType type) {
        List<FreeAccountNumber> freeNumbers = freeAccountNumbersRepository.findAccountNumbersByType(type);
        if (freeNumbers.isEmpty()) {
            log.info("No free account numbers for type {}. Generating new batch...", type);
            generateAccountNumbers(type, 100);
            freeNumbers = freeAccountNumbersRepository.findAccountNumbersByType(type);
            if (freeNumbers.isEmpty()) {
                throw new IllegalStateException("Failed to generate free account numbers for type " + type);
            }
        }
        FreeAccountNumber accountNumber = freeNumbers.get(0);
        freeAccountNumbersRepository.deleteById(accountNumber.getId());
        return accountNumber.getId().getAccountNumber();
    }
}