package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountSeq;
import faang.school.accountservice.entity.AccountType;
import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.repository.AccountSeqRepository;
import faang.school.accountservice.repository.FreeAccountRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FreeAccountNumberService {
    private static final long MAX_ACCOUNT_COUNTER = 1_0000_0000_0000L;
    private final AccountSeqRepository accountSeqRepository;
    private final FreeAccountRepository freeAccountRepository;
    private final Map<AccountType, Long> accountTypesMap;

    public FreeAccountNumberService(AccountSeqRepository accountSeqRepository,
                                    FreeAccountRepository freeAccountRepository,
                                    List<AccountNumber> accountNumbers) {

        this.accountSeqRepository = accountSeqRepository;
        this.freeAccountRepository = freeAccountRepository;
        this.accountTypesMap = accountNumbers.stream()
                .collect(Collectors.toMap(
                        AccountNumber::getAccountType,
                        AccountNumber::getAccountNumberPattern
                ));
    }

    @Transactional
    public void generateAccountNumbers(AccountType type, int batchSize) {
        log.info("Generating account numbers for type: {}", type);
        Long accountPattern = accountTypesMap.get(type);
        if (accountPattern == null) {
            throw new RuntimeException(String.format("Unknown type %s", type.name()));
        }
        if (accountSeqRepository.findByType(type).isEmpty()) {
            initSequenceForType(type);
        }
        long startCount = accountSeqRepository.findByType(type).get().getCounter();
        accountSeqRepository.incrementCounter(type.name(), batchSize);
        if ((startCount + batchSize) >= MAX_ACCOUNT_COUNTER) {
            throw new RuntimeException("The maximum account number has been reached");
        }
        List<FreeAccountNumber> numbers = new ArrayList<>();
        for (long i = startCount; i < (startCount + batchSize); i++) {
            numbers.add(new FreeAccountNumber(new FreeAccountId(type, accountPattern + i)));
        }
        freeAccountRepository.saveAll(numbers);
    }

    @Transactional
    public void retrieveAccountNumber(AccountType type, Consumer<FreeAccountNumber> numberConsumer) {
        numberConsumer.accept(freeAccountRepository.retrieveFirst(type.name()));
    }

    @Transactional
    private void initSequenceForType(AccountType type) {
        AccountSeq initialSeq = new AccountSeq();
        initialSeq.setType(type);
        initialSeq.setCounter(0);
        accountSeqRepository.save(initialSeq);
        log.info("Init seq for type = {}", type.name());
    }
}
