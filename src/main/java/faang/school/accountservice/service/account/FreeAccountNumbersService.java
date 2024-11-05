package faang.school.accountservice.service.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import faang.school.accountservice.entity.account.AccountNumbersSequence;
import faang.school.accountservice.entity.account.FreeAccountId;
import faang.school.accountservice.entity.account.FreeAccountNumber;
import faang.school.accountservice.enums.account.AccountEnum;
import faang.school.accountservice.repository.account.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.account.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {

    private final static long ACCOUNT_PATTERN = 4200_0000_0000_0000L;

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Transactional
    public void generateAccountNumbers(AccountEnum type, int batchSize) {
        AccountNumbersSequence ans = accountNumbersSequenceRepository.findCounterByType(type.name())
            .orElseGet(() -> generateFirstAccountNumberSequenceForType(type));

        List<FreeAccountNumber> numberList = new ArrayList<>();
        for (long i = ans.getInitialValue(); i < ans.getCounter(); i++) {
            FreeAccountId freeAccountId = new FreeAccountId(type, Long.toString(ACCOUNT_PATTERN + i));
            FreeAccountNumber freeAccountNumber = new FreeAccountNumber(freeAccountId);
            numberList.add(freeAccountNumber);
        }
        freeAccountNumbersRepository.saveAll(numberList);
        accountNumbersSequenceRepository.updateCounterForType(type.name(), batchSize);
    }

    @Transactional
    public void retrieveAccountNumber(AccountEnum type, Consumer<FreeAccountNumber> numberConsumer) {
        FreeAccountNumber freeAccountNumber = freeAccountNumbersRepository.findFirstFreeAccountNumberByType(type.name())
            .orElseGet(() -> generateFreeAccountNumber(type));
        
        freeAccountNumbersRepository.deleteFreeAccountNumberByTypeAndAccountNumber(
            type.name(), 
            freeAccountNumber.getId().getAccountNumber()
        );
        
        numberConsumer.accept(freeAccountNumber);
    }

    @Transactional
    private FreeAccountNumber generateFreeAccountNumber(AccountEnum type) {
        AccountNumbersSequence ans = accountNumbersSequenceRepository.findCounterByType(type.name())
            .orElseGet(() -> {
                return generateFirstAccountNumberSequenceForType(type);
            });

        long initialValue = ans.getInitialValue();
        FreeAccountId freeAccountId = new FreeAccountId(type, Long.toString(ACCOUNT_PATTERN + initialValue));
        FreeAccountNumber freeAccountNumber = new FreeAccountNumber(freeAccountId);
        freeAccountNumbersRepository.save(freeAccountNumber);
        return freeAccountNumber;
    }

    @Transactional
    private AccountNumbersSequence generateFirstAccountNumberSequenceForType(AccountEnum type) {
        AccountNumbersSequence ans = new AccountNumbersSequence();
        ans.setType(type.name());
        ans.setCounter(1);
        accountNumbersSequenceRepository.save(ans);
        return ans;
    }


}
