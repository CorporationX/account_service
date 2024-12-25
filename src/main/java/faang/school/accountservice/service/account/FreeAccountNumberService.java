
package faang.school.accountservice.service.account;

import faang.school.accountservice.config.account.FreeAccountNumbersConfig;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.account.freeaccounts.FreeAccountNumber;
import faang.school.accountservice.model.account.sequence.AccountSeq;
import faang.school.accountservice.repository.account.AccountNumberPrefixRepository;
import faang.school.accountservice.repository.account.freeaccounts.FreeAccountRepository;
import faang.school.accountservice.repository.account.sequence.AccountNumbersSequenceRepository;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FreeAccountNumberService {
    private final FreeAccountRepository freeAccountRepository;
    private final AccountNumberPrefixRepository accountNumberPrefixRepository;
    private final FreeAccountNumbersConfig freeAccountNumbersConfig;

    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    private static final Logger log = LoggerFactory.getLogger(FreeAccountNumberService.class);

    @Transactional
    public String processAccountNumber(AccountType accountType) {
        log.info("Attempting to retrieve and delete first free account number for type: {}", accountType.name());
        int freeAccountCount = freeAccountRepository.countFreeAccountNumberByType(accountType);
        if (freeAccountCount == 0) {
            log.warn("No free account number found for type: {}", accountType);
            createFreeAccounts(accountType, freeAccountNumbersConfig.getAccountNumberLength(), 1);
        }
        FreeAccountNumber freeAccount = freeAccountRepository.retrieveAndDeleteFirst(accountType.name())
                .orElseThrow(() -> new NoSuchElementException("No free account found for account type: " + accountType));
        log.info("Account number retrieved and deleted: {}", freeAccount.getAccountNumber());
        return freeAccount.getAccountNumber();
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public void generateFreeAccountNumbersWithBatchSize(AccountType accountType, long accountLength, long accountQuantity) {
        createFreeAccounts(accountType, accountLength, accountQuantity);
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public void generateFreeAccountNumbersWithLimit(AccountType accountType, long accountLength, long limit) {
        long numberOfAccountsToCreate = limit - freeAccountRepository.countFreeAccountNumberByType(accountType);
        if (numberOfAccountsToCreate <= 0) {
            log.info("Number of total accounts is achieved. No new accounts have been created");
        } else {
            createFreeAccounts(accountType, accountLength, numberOfAccountsToCreate);
        }
    }

    private void createFreeAccounts(AccountType accountType, long accountLength, long accountQuantity) {
        if (accountLength < 12 || accountLength > 20) {
            throw new IllegalArgumentException("Invalid account number length");
        }
        List<String> accountNumbers = new ArrayList<>();
        String prefix = accountNumberPrefixRepository.findAccountNumberPrefixByType(accountType);
        AccountSeq accountSequence = accountNumbersSequenceRepository.findByType(accountType)
                .orElseGet(() -> accountNumbersSequenceRepository.createCounter(accountType));
        long sequenceNumber = accountSequence.getCounter();
        long initialSequenceNumber = sequenceNumber;
        for (int i = 0; i < accountQuantity; i++) {
            accountNumbers.add(generateAccountNumber(prefix, accountLength, sequenceNumber));
            sequenceNumber++;
        }
        List<FreeAccountNumber> freeAccounts = accountNumbers.stream()
                .map(accountNumber -> FreeAccountNumber
                        .builder()
                        .type(accountType)
                        .accountNumber(accountNumber)
                        .build())
                .toList();
        accountNumbersSequenceRepository.incrementCounter(accountType, initialSequenceNumber, accountQuantity);
        freeAccountRepository.saveAll(freeAccounts);
    }

    private String generateAccountNumber(String prefix, long length, long sequenceNumber) {
        long sequenceNumberLength = length - prefix.length();
        return prefix + String.format("%0" + sequenceNumberLength + "d", sequenceNumber);
    }
}
