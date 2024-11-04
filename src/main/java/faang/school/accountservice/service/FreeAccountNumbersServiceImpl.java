package faang.school.accountservice.service;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountSeqRepository;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersServiceImpl implements FreeAccountNumbersService{

    private static final long ACCOUNT_PATTERN = 4200_0000_0000_0000L;

    private final FreeAccountNumberRepository freeAccountNumberRepository;
    private final AccountSeqRepository accountSeqRepository;

    @Transactional
    public void generateAccountNumbers(AccountType type, int batchSize) {
        long period = accountSeqRepository.findByType(type.name()).getCounter();
        accountSeqRepository.incrementCounter(type.name(), batchSize);
        long updatedCount = accountSeqRepository.findByType(type.name()).getCounter();

        List<FreeAccountNumber> numbers = new ArrayList<>();
        for (long i = period; i < updatedCount; i++) {
            FreeAccountNumber accountNumber = new FreeAccountNumber();
            accountNumber.setAccountType(type);
            accountNumber.setAccountNumber(ACCOUNT_PATTERN + i);

            numbers.add(accountNumber);
        }
        freeAccountNumberRepository.saveAll(numbers);
    }

    @Transactional
    public void retrieveAccountNumber(AccountType type, Consumer<FreeAccountNumber> numberConsumer) {
        numberConsumer.accept(freeAccountNumberRepository.retrieveFirst(type.name()));
    }

}