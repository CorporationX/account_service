package faang.school.accountservice.service;

import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSeqRepository;
import faang.school.accountservice.repository.FreeAccountRepository;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Validated
public class FreeAccountNumberService {
    private static final long DEBIT_PATTERN = 4200_0000_0000_0000L;
    private static final long SAVINGS_PATTERN = 5236_0000_0000_0000L;

    private final AccountNumbersSeqRepository accountSeqRepository;
    private final FreeAccountRepository freeAccountRepository;

    @Transactional
    public void generateAccountNumbers(AccountType type, @Positive int batchSize) {
        long pattern = getPattern(type);
        accountSeqRepository.createNumbersSequenceIfNecessary(type);
        long initial = accountSeqRepository.generateAccountNumbers(type, batchSize);
        List<FreeAccountNumber> newNumbers = new ArrayList<>();
        for (long i = initial; i < initial + batchSize; i++) {
            newNumbers.add(new FreeAccountNumber(new FreeAccountId(type, pattern + i)));
        }
        freeAccountRepository.saveAll(newNumbers);
    }

    @Transactional
    public void retrieveAccountNumber(AccountType type, Consumer<FreeAccountNumber> numberConsumer) {
        numberConsumer.accept(freeAccountRepository.retrieveNumber(type));
    }

    private long getPattern(AccountType type) {
        if (type == AccountType.DEBIT) {
            return DEBIT_PATTERN;
        } else {
            return SAVINGS_PATTERN;
        }
    }
}
