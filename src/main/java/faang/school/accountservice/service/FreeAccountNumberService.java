package faang.school.accountservice.service;

import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.mapper.FreeAccountNumberMapper;
import faang.school.accountservice.model.AccountNumbersSequence;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.LongStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class FreeAccountNumberService {
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final FreeAccountNumberMapper freeAccountNumberMapper;

    @Value("${account.number.length}")
    private int accountNumberLength;
    @Value("${account.type.length}")
    private int typeCodeLength;

    @Transactional
    public String getFreeAccountNumber(AccountType accountType) {
        return freeAccountNumbersRepository
                .getFreeAccountNumber(accountType.getCode())
                .orElse(generateSingleFreeAccountNumber(accountType));
    }

    @Transactional
    public Integer getQuantityFreeAccountNumbersByType(AccountType accountType) {
        return freeAccountNumbersRepository.getFreeAccountNumbersCountByType(accountType.getCode());
    }

    @Transactional
    public List<FreeAccountNumber> generateAccountNumbers(AccountType accountType, int batchSize) {
        checkAccountTypeExist(accountType);
        long currentNumber = incrementAndGetSequenceNumber(accountType, batchSize);
        long initial = currentNumber - batchSize;
        List<FreeAccountNumber> freeAccountNumbers = LongStream
                .range(initial, currentNumber)
                .mapToObj(number -> freeAccountNumberMapper
                        .toFreeAccountNumber(accountType, number, accountNumberLength - typeCodeLength))
                .toList();
        freeAccountNumbersRepository.saveAll(freeAccountNumbers);
        log.info("Generated free account numbers: {}", freeAccountNumbers.size());
        return freeAccountNumbers;
    }

    private long incrementAndGetSequenceNumber(AccountType accountType, int batchSize) {
        AccountNumbersSequence sequence = accountNumbersSequenceRepository
                .incrementCounter(accountType.getCode(), batchSize);
        return sequence.getCurrentNumber();
    }

    private String generateSingleFreeAccountNumber(AccountType accountType) {
        checkAccountTypeExist(accountType);
        long currentNumber = incrementAndGetSequenceNumber(accountType, 1);
        long initial = currentNumber - 1;
        FreeAccountNumber freeAccountNumber = freeAccountNumberMapper
                .toFreeAccountNumber(accountType, initial, accountNumberLength - typeCodeLength);
        return freeAccountNumber.getAccountNumber();
    }

    private void checkAccountTypeExist(AccountType accountType) {
        if (!accountNumbersSequenceRepository.existsById(accountType.getCode())) {
            saveSequenceWithNewType(accountType);
            log.info("New sequence created for account type: {}", accountType.name());
        }
    }

    private void saveSequenceWithNewType(AccountType accountType) {
        AccountNumbersSequence accountNumbersSequence = AccountNumbersSequence.builder()
                .accountType(accountType.getCode())
                .build();
        accountNumbersSequenceRepository.save(accountNumbersSequence);
    }
}
