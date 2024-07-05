package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.AccountNumberException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeAccountNumbersService {
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Transactional
    public void generateAccountNumbers(AccountType accountType, int batchSize) {
        List<FreeAccountNumber> accountNumbers = new ArrayList<>();
        AccountNumberSequence ans = accountNumbersSequenceRepository.incrementCounter(accountType.name(), batchSize);

        for (long i = ans.getPreviousCounter(); i < ans.getCurrentCounter(); i++) {
            accountNumbers.add(new FreeAccountNumber(accountType, accountType.getPattern() + i));
        }
        log.info("Generated {} {} account number", batchSize, accountType);

        freeAccountNumbersRepository.saveAll(accountNumbers);
    }

    @Transactional
    public void generateAccountNumbersUpToQuantity(AccountType accountType, int quantity) {
        int requiredQuantity = quantity - freeAccountNumbersRepository.getCurrentQuantityOfNumbersByType(accountType.name());

        if (requiredQuantity > 0) {
            generateAccountNumbers(accountType, requiredQuantity);
        } else {
            log.info("There are already " + quantity + " available account numbers type: "
                    + accountType.name() + " in the database");
            throw new AccountNumberException("There are already " + quantity
                    + " available account numbers type: " + accountType.name() + " in the database");
        }
    }

    @Transactional
    public void retrieveAndHandleAccountNumber(AccountType accountType, Consumer<FreeAccountNumber> numberConsumer) {
        FreeAccountNumber freeAccountNumber = freeAccountNumbersRepository.getAndDeleteAccountByType(accountType.name());

        if (freeAccountNumber == null) {
            generateAccountNumbers(accountType, 1);
        }
        numberConsumer.accept(freeAccountNumbersRepository.getAndDeleteAccountByType(accountType.name()));
    }
}
