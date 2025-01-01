package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountSeq;
import faang.school.accountservice.entity.AccountType;
import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccoutNumber;
import faang.school.accountservice.repository.AccountSeqRepository;
import faang.school.accountservice.repository.FreeAccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumberService {

    private static final long ACCOUNT_PATTERN = 4200_0000_0000_0000L;

    private final AccountSeqRepository accountSeqRepository;
    private final FreeAccountRepository freeAccountRepository;

    @Transactional
    public void generateAccountNumbers(AccountType type,int batchSize){
        AccountSeq period =accountSeqRepository.incrementCounter(type.name(),batchSize);
        List<FreeAccoutNumber> numbers = new ArrayList<>();
        for (long i = period.getInitialValue();i < period.getCounter(); i++){
            numbers.add(new FreeAccoutNumber(new FreeAccountId(type,ACCOUNT_PATTERN+ i)));
        }
        freeAccountRepository.saveAll(numbers);
    }
    @Transactional
    public void retrieverAccountNumber(AccountType type, Consumer<FreeAccoutNumber> numberConsumer){
        numberConsumer.accept(freeAccountRepository.retrieveFirst(type.name()));
    }
}
