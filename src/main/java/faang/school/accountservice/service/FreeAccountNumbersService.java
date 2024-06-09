package faang.school.accountservice.service;

import faang.school.accountservice.model.AccountNumber;
import faang.school.accountservice.model.AccountNumberId;
import faang.school.accountservice.model.AccountSequence;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {
    private final static long DEBIT = 4200_0000_0000_0000L;
    private final static long CREDIT = 5236_0000_0000_0000L;
    @Value("${account_numbers.batchSize}")
    private int batchSize;

    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Transactional
    public void generatedNumbers(String type) {
        AccountSequence accountSequence = accountNumbersSequenceRepository.incrementCounter(type, batchSize);
        long start = accountSequence.getInitialValue();
        long end = accountSequence.getCounter();
        long typeNumber = getTypeNumber(type);
        List<AccountNumber> accountNumbers = new ArrayList<>();
        for (long i = start; i < end; i++) {
            AccountNumberId accountNumberId = new AccountNumberId(typeNumber + i, type);
            AccountNumber accountNumber = new AccountNumber(accountNumberId);
            accountNumbers.add(accountNumber);
        }
        freeAccountNumbersRepository.saveAll(accountNumbers);
    }

    @Transactional
    public void createAccountNumber(String type, Consumer<AccountNumber> consumer) {
        validationNumber(type);
        Optional<AccountNumber> accountNumber = freeAccountNumbersRepository.getFreeNumber(type);
        consumer.accept(freeAccountNumbersRepository.save(accountNumber.get()));
    }

    private long getTypeNumber(String type) {
        if (type.equals("debit")) {
            return DEBIT;
        } else {
            return CREDIT;
        }
    }

    private void validationNumber(String type) {
        if (freeAccountNumbersRepository.getFreeNumber(type).isEmpty()) {
            generatedNumbers(type);
        }
    }
}