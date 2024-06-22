package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Value("${account.number.batch.size}")
    private int batchSize;

    private final long SAVINGS_PATTERN = 5236_0000_0000_0000L;
    private final long DEBIT_PATTERN = 4200_0000_0000_0000L;

    @Transactional
    public void generateAccountNumbers(AccountType accountType) {
        List<FreeAccountNumber> accountNumbers = new ArrayList<>();
        AccountNumberSequence ans = accountNumbersSequenceRepository.incrementCounter(accountType.name(), batchSize);

        if (accountType == AccountType.SAVINGS) {
            for (long i = ans.getPreviousCounter(); i < ans.getCurrentCounter(); i++) {
                accountNumbers.add(new FreeAccountNumber(accountType, SAVINGS_PATTERN + i));
            }
        } else if (accountType == AccountType.DEBIT) {
            for (long i = ans.getPreviousCounter(); i < ans.getCurrentCounter(); i++) {
                accountNumbers.add(new FreeAccountNumber(accountType, DEBIT_PATTERN + i));
            }
        }

        freeAccountNumbersRepository.saveAll(accountNumbers);
    }

    @Transactional
    public void getAndHandleAccountNumber(AccountType accountType, Consumer<FreeAccountNumber> numberConsumer) {
        FreeAccountNumber freeAccountNumber = freeAccountNumbersRepository.getAndDeleteAccountByType(accountType.name());

        if (freeAccountNumber == null) {
            generateAccountNumbers(accountType);
            numberConsumer.accept(freeAccountNumbersRepository.getAndDeleteAccountByType(accountType.name()));
        } else {
            numberConsumer.accept(freeAccountNumber);
        }
    }
}
