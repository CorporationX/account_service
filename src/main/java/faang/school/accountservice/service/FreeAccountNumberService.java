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
                .orElseGet(() -> generateAccountNumbers(accountType, 1, false).get(0).getAccountNumber());
    }

    @Transactional
    public Integer getQuantityFreeAccountNumbersByType(AccountType accountType) {
        return freeAccountNumbersRepository.getFreeAccountNumbersCountByType(accountType.getCode());
    }

    @Transactional
    public List<FreeAccountNumber> generateAccountNumbers(AccountType accountType, int batchSize, boolean isSave) {
        if (!accountNumbersSequenceRepository.existsById(accountType.getCode())) {
            saveSequenceWithNewType(accountType);
        }
        AccountNumbersSequence sequence = accountNumbersSequenceRepository
                .incrementCounter(accountType.getCode(), batchSize);
        long currentNumber = sequence.getCurrentNumber();
        long initial = currentNumber - batchSize;
        List<FreeAccountNumber> freeAccountNumbers = LongStream
                .range(initial, currentNumber)
                .mapToObj(number -> freeAccountNumberMapper
                        .toFreeAccountNumber(accountType, number, accountNumberLength - typeCodeLength))
                .toList();
        if (isSave) {
            freeAccountNumbersRepository.saveAll(freeAccountNumbers);
        }
        log.info("Generated free account numbers: {}", freeAccountNumbers.size());
        return freeAccountNumbers;
    }

    private void saveSequenceWithNewType(AccountType accountType) {
        AccountNumbersSequence accountNumbersSequence = AccountNumbersSequence.builder()
                .accountType(accountType.getCode())
                .build();
        accountNumbersSequenceRepository.save(accountNumbersSequence);
    }
}
