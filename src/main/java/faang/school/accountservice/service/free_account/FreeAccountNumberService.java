package faang.school.accountservice.service.free_account;

import faang.school.accountservice.entity.free_account.AccountNumberSequence;
import faang.school.accountservice.entity.free_account.FreeAccountNumber;
import faang.school.accountservice.entity.free_account.FreeAccountNumberId;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.free_account.AccountNumberSequenceRepository;
import faang.school.accountservice.repository.free_account.FreeAccountNumberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumberService {

    private static final Long DEFAULT_NUMBER = 0L;

    @Value("${account.type.number.batch-size}")
    private int batchSize;

    @Value("${account.number-length}")
    private int numberLength;

    @Value("${account.type.number.first-numbers}")
    private Map<AccountType, Integer> firstNumbers;

    private final AccountNumberSequenceRepository accountNumberSequenceRepository;
    private final FreeAccountNumberRepository freeAccountNumberRepository;

    private void createAccountNumberSequenceIfNotExists(AccountType accountType) {
        if (!checkAccountNumberSequence(accountType)) {
            AccountNumberSequence accountNumberSequence = new AccountNumberSequence();
            accountNumberSequence.setAccountType(accountType);
            accountNumberSequence.setLastValue(DEFAULT_NUMBER);
            accountNumberSequenceRepository.save(accountNumberSequence);
        }
    }

    private FreeAccountNumber createFreeAccountNumber(AccountType accountType, Long accountNumber) {
        FreeAccountNumberId freeAccountNumberId = new FreeAccountNumberId();
        freeAccountNumberId.setAccountType(accountType);
        freeAccountNumberId.setAccountNumber(accountNumber);

        FreeAccountNumber freeAccountNumbers = new FreeAccountNumber();
        freeAccountNumbers.setFreeAccountNumberId(freeAccountNumberId);

        return freeAccountNumbers;
    }

    @Transactional
    public boolean tryIncrement(AccountType accountType, Long expectedValue) {
        return accountNumberSequenceRepository.incrementAccountNumberIfMatch(accountType, expectedValue) == 1;
    }

    @Transactional
    public void generateAccountNumber(AccountType accountType) {
        createAccountNumberSequenceIfNotExists(accountType);
        List<FreeAccountNumber> freeAccountNumbers = new ArrayList<>();

        for (int i = 0; i < batchSize; i++) {
            Long accountNumber = incrementAndGenerateNewNumber(accountType);
            freeAccountNumbers.add(createFreeAccountNumber(accountType, accountNumber));
        }

        freeAccountNumberRepository.saveAll(freeAccountNumbers);
    }

    private boolean checkAccountNumberSequence(AccountType accountType) {
        return accountNumberSequenceRepository.existsById(accountType);
    }

    private Long incrementAndGenerateNewNumber(AccountType accountType) {
        Long currentValue;
        boolean updated;
        do {
            currentValue = accountNumberSequenceRepository.findLastValueByAccountType(accountType);
            updated = tryIncrement(accountType, currentValue);
        } while (!updated);

        Integer firstNumber = firstNumbers.get(accountType);
        if (firstNumber == null) {
            throw new IllegalStateException("First number not configured for account type: " + accountType);
        }

        String accNumber = firstNumber + String.format("%0" + numberLength + "d", currentValue);
        return Long.parseLong(accNumber);
    }

    @Transactional
    public void useFreeAccountNumber(AccountType accountType, Consumer<Long> accountNumberConsumer) {
        createAccountNumberSequenceIfNotExists(accountType);

        Long accountNumber;
        Optional<FreeAccountNumber> optionalFreeAccountNumber = freeAccountNumberRepository.deleteFirst(accountType.name());
        if (optionalFreeAccountNumber.isPresent()) {
            accountNumber = optionalFreeAccountNumber.get().getFreeAccountNumberId().getAccountNumber();
        } else {
            accountNumber = incrementAndGenerateNewNumber(accountType);
        }

        accountNumberConsumer.accept(accountNumber);
    }
}
