package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountSeq;
import faang.school.accountservice.entity.AccountType;
import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.repository.AccountSeqRepository;
import faang.school.accountservice.repository.FreeAccountRepository;
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
public class FreeAccountNumberService {
    private static final long MAX_ACCOUNT_PATTERN = 1_0000_0000_0000L;
    private static final long DEBIT_ACCOUNT_PATTERN = 4200_0000_0000_0000L;
    private static final long CREDIT_ACCOUNT_PATTERN = 5236_0000_0000_0000L;
    private final AccountSeqRepository accountSeqRepository;
    private final FreeAccountRepository freeAccountRepository;

    @Transactional
    public void generateAccountNumbers(AccountType type, int batchSize) {
        long accountPattern = 0L;
        switch (type) {
            case DEBIT -> accountPattern = DEBIT_ACCOUNT_PATTERN;
            case CREDIT -> accountPattern = CREDIT_ACCOUNT_PATTERN;
            default -> throw new RuntimeException(String.format("Unknown type %s ", type.name()));
        }
        AccountSeq period = accountSeqRepository.incrementCounter(type.name(), batchSize);
        if (period.getCounter() >= MAX_ACCOUNT_PATTERN) {
            throw new RuntimeException("The maximum account number has been reached");
        }
        List<FreeAccountNumber> numbers = new ArrayList<>();
        for (long i = period.getInitialValue(); i < period.getCounter(); i++) {
            numbers.add(new FreeAccountNumber(new FreeAccountId(type, accountPattern + i)));
        }
        freeAccountRepository.saveAll(numbers);
    }

    @Transactional
    public void retrieveAccountNumber(AccountType type, Consumer<FreeAccountNumber> numberConsumer) {
        numberConsumer.accept(freeAccountRepository.retrieveFirst(type.name()));
    }
}
