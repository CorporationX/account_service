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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumberService {
    private static final long MAX_ACCOUNT_COUNTER = 1_0000_0000_0000L;
    private final AccountSeqRepository accountSeqRepository;
    private final FreeAccountRepository freeAccountRepository;
    private final Map<AccountType, Long> accountTypes = new HashMap<>();

    public void addAccountType(AccountType type, Long pattern) {
        accountTypes.put(type, pattern);
    }

    @Transactional
    public void generateAccountNumbers(AccountType type, int batchSize) {
        log.info("Generating account numbers for type: {}", type);
        Long accountPattern = accountTypes.get(type);
        if (accountPattern == null) {
            throw new RuntimeException(String.format("Unknown type %s", type.name()));
        }
        AccountSeq sequence = accountSeqRepository.incrementCounter(type.name(), batchSize);
        if (sequence.getCounter() >= MAX_ACCOUNT_COUNTER) {
            throw new RuntimeException("The maximum account number has been reached");
        }
        List<FreeAccountNumber> numbers = new ArrayList<>();
        for (long i = (sequence.getCounter() - batchSize); i < sequence.getCounter(); i++) {
            numbers.add(new FreeAccountNumber(new FreeAccountId(type, accountPattern + i)));
        }
        freeAccountRepository.saveAll(numbers);
    }

    @Transactional
    public void retrieveAccountNumber(AccountType type, Consumer<FreeAccountNumber> numberConsumer) {
        numberConsumer.accept(freeAccountRepository.retrieveFirst(type.name()));
    }
}
