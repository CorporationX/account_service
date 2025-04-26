package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountSeq;
import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {
    private static final long ACCOUNT_PATTERN = 4200_0000_0000_0000L;

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Transactional
    public void generatedAccountNumbers(AccountType type, int batchSize) {
        AccountSeq period = accountNumbersSequenceRepository.incrementCounter(type.name(), batchSize);
        List<FreeAccountNumber> numberList = new ArrayList<>();
        for (long i = period.getInitialValue(); i < period.getCounter(); i++) {
            numberList.add(new FreeAccountNumber(new FreeAccountId(type, ACCOUNT_PATTERN + i)));
        }
        freeAccountNumbersRepository.saveAll(numberList);
    }

    @Transactional
    public void retrieveAccountNumber(AccountType accountType, Consumer<FreeAccountNumber> numberConsumer) {
        numberConsumer.accept(freeAccountNumbersRepository.retrieveFirst(accountType.name()));
    }
}