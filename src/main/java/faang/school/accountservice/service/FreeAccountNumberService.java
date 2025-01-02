package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumberSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FreeAccountNumberService {
    public static final String DEBIT_ACCOUNT_PATTERN = "4200000000000000";
    public static final String CREDIT_ACCOUNT_PATTERN = "5236000000000000";
    public static final int SINGLE_EXTRA_ACCOUNT_NUMBER = 1;
    private final AccountNumberSequenceRepository accountNumberSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Transactional
    public void generateFreeAccountNumbers(AccountType type, int batchSize, String accPattern) {
        AccountNumberSequence period = accountNumberSequenceRepository.incrementCounter(type.name(), batchSize);
        List<FreeAccountNumber> numbers = new ArrayList<>();
        for (long i = period.getInitialValue(); i < period.getCounter(); i++) {
            String mutatedAccNumber = mutateAccountNumber(accPattern, batchSize);
            FreeAccountNumber accNumber = FreeAccountNumber.builder()
                    .id(new FreeAccountId(Long.parseLong(mutatedAccNumber), type))
                    .build();
            numbers.add(accNumber);
        }
        freeAccountNumbersRepository.saveAll(numbers);
    }

    @Transactional
    public FreeAccountNumber getFreeAccountNumber(AccountType type, String accPattern) {
        FreeAccountNumber freeAccNumber = freeAccountNumbersRepository.deleteFirst(type.name());
        return freeAccNumber == null ? generateSingleFreeAccountNumber(type, accPattern) : freeAccNumber;
    }

    @Transactional
    private FreeAccountNumber generateSingleFreeAccountNumber(AccountType type, String accPattern) {
        accountNumberSequenceRepository.incrementCounter(type.name(), SINGLE_EXTRA_ACCOUNT_NUMBER);
        String mutatedAccNumber = mutateAccountNumber(accPattern, SINGLE_EXTRA_ACCOUNT_NUMBER);
        FreeAccountNumber accNumber = FreeAccountNumber.builder()
                .id(new FreeAccountId(Long.parseLong(mutatedAccNumber), type))
                .build();
        return freeAccountNumbersRepository.save(accNumber);
    }

    private String mutateAccountNumber(String accPattern, int batchSize) {
        String firstFourDigits = accPattern.substring(0, 4);
        String mutatedPart = String.format("%0" + (accPattern.length() - 4) + "d", batchSize);
        return firstFourDigits + mutatedPart;
    }
}
