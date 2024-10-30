package faang.school.accountservice.service.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import faang.school.accountservice.entity.account.AccountNumbersSequence;
import faang.school.accountservice.entity.account.FreeAccountNumber;
import faang.school.accountservice.enums.account.AccountEnum;
import faang.school.accountservice.repository.account.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.account.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;


    @Transactional
    public void processAccountNumber(AccountEnum accountType, Consumer<String> action) {
        log.info("Processing account number for account type: {}", accountType);
        String accountNumber = freeAccountNumbersRepository.findAndRemoveFreeAccountNumber(accountType);

        if (accountNumber == null) {
            log.info("No free account number available. Generating a new account number for account type: {}", accountType);
            accountNumber = generateNewAccountNumber(accountType);
        }

        if (accountNumber != null) {
            log.info("Account number generated: {}", accountNumber);
            action.accept(accountNumber);
        } else {
            throw new IllegalStateException(String.format("Failed to generate or retrieve an account number for account type: {}", accountType));
        }
    }

    private String generateNewAccountNumber(AccountEnum accountType) {
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findById(accountType)
            .orElseGet(() -> createCounterForAccountType(accountType));

        boolean hasIncremented = accountNumbersSequenceRepository.incrementCounterForAccountType(accountType, sequence.getCurrentCounter());

        if (hasIncremented) {
            String newAccountNumber = accountType.getPrefix() + String.format("%12d", sequence.getCurrentCounter() + 1);
            createFreeAccountNumber(accountType, newAccountNumber);
            log.info("New account number generated for account type {}: {}", accountType, newAccountNumber);
            return newAccountNumber;
        }
        log.warn("Failed to increment counter for account type: {}", accountType);
        return null;
    }

    private AccountNumbersSequence createCounterForAccountType(AccountEnum accountType) {
        AccountNumbersSequence sequence = new AccountNumbersSequence();
        sequence.setAccountType(accountType);
        sequence.setCurrentCounter(0L);
        AccountNumbersSequence savedSequence = accountNumbersSequenceRepository.save(sequence);
        log.info("Created new counter for account type {} with initial value 0", accountType);
        return savedSequence;
    }

    private FreeAccountNumber createFreeAccountNumber(AccountEnum accountType, String freeAccountNumber) {
        FreeAccountNumber entity = new FreeAccountNumber();
        entity.setAccountType(accountType);
        entity.setFreeAccountNumber(freeAccountNumber);
        FreeAccountNumber savedEntity = freeAccountNumbersRepository.save(entity);
        log.info("Free account number {} saved for account type {}", freeAccountNumber, accountType);
        return savedEntity;
    }
}
