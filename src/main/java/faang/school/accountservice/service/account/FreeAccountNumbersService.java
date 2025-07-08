package faang.school.accountservice.service.account;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exceptions.DataValidationException;
import faang.school.accountservice.model.AccountNumbersSequence;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeAccountNumbersService {

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository sequenceRepository;

    private static final Map<AccountType, String> ACCOUNT_TYPE_PREFIXES = Map.of(
            AccountType.PERSONAL_PHYSICAL, "4200",
            AccountType.PERSONAL_LEGAL, "4201",
            AccountType.CURRENCY, "4202",
            AccountType.SAVINGS, "5236",
            AccountType.CORPORATE, "4203"
    );

    private static final int ACCOUNT_NUMBER_LENGTH = 16;
    private static final int MAX_RETRY_ATTEMPTS = 10;

    @Transactional
    public void generateAndSaveFreeAccountNumbers(AccountType accountType, int count) {
        if (count <= 0) {
            throw new DataValidationException("Count must be positive");
        }

        log.info("Generating {} free account numbers for type: {}", count, accountType);

        for (int i = 0; i < count; i++) {
            String accountNumber = generateNewAccountNumber(accountType);
            if (accountNumber != null) {
                saveFreeAccountNumber(accountType, accountNumber);
            } else {
                log.warn("Failed to generate account number {} of {}", i + 1, count);
            }
        }
    }

    @Transactional
    public void saveFreeAccountNumber(AccountType accountType, String accountNumber) {
        validateAccountNumber(accountNumber);

        FreeAccountNumber freeAccountNumber = FreeAccountNumber.builder()
                .accountType(accountType)
                .accountNumber(accountNumber)
                .build();

        freeAccountNumbersRepository.save(freeAccountNumber);
        log.debug("Saved free account number: {} for type: {}", accountNumber, accountType);
    }

    @Transactional
    public void executeWithFreeAccountNumber(AccountType accountType, Consumer<String> action) {
        String accountNumber = getAndRemoveFreeAccountNumber(accountType);
        if (accountNumber == null) {
            accountNumber = generateNewAccountNumber(accountType);
            if (accountNumber == null) {
                throw new DataValidationException("Unable to generate account number for type: " + accountType);
            }
        }

        try {
            action.accept(accountNumber);
            log.info("Successfully executed action with account number: {}", accountNumber);
        } catch (Exception e) {
            log.error("Failed to execute action with account number: {}", accountNumber, e);
            saveFreeAccountNumber(accountType, accountNumber);
            throw e;
        }
    }

    @Transactional
    public String getAndRemoveFreeAccountNumber(AccountType accountType) {
        Optional<String> accountNumber = freeAccountNumbersRepository
                .findFirstAndDeleteByAccountType(accountType.name());

        if (accountNumber.isPresent()) {
            log.debug("Retrieved and removed free account number: {} for type: {}",
                    accountNumber.get(), accountType);
            return accountNumber.get();
        }

        log.debug("No free account numbers available for type: {}", accountType);
        return null;
    }

    @Transactional
    public void initializeSequence(AccountType accountType) {
        if (sequenceRepository.findByAccountType(accountType).isEmpty()) {
            AccountNumbersSequence sequence = AccountNumbersSequence.builder()
                    .accountType(accountType)
                    .currentValue(0L)
                    .version(0L)
                    .build();

            sequenceRepository.save(sequence);
            log.info("Initialized sequence for account type: {}", accountType);
        }
    }

    public long getFreeAccountNumbersCount(AccountType accountType) {
        return freeAccountNumbersRepository.countByAccountType(accountType);
    }

    private String generateNewAccountNumber(AccountType accountType) {
        String prefix = ACCOUNT_TYPE_PREFIXES.get(accountType);
        if (prefix == null) {
            throw new DataValidationException("No prefix configured for account type: " + accountType);
        }

        for (int attempts = 0; attempts < MAX_RETRY_ATTEMPTS; attempts++) {
            Optional<Long> currentValueOpt = sequenceRepository.getCurrentValue(accountType);
            Long currentValue;

            if (currentValueOpt.isEmpty()) {
                initializeSequence(accountType);
                currentValue = 0L;
            } else {
                currentValue = currentValueOpt.get();
            }

            long nextValue = currentValue + 1;
            int updatedRows = sequenceRepository.incrementSequence(accountType, currentValue);

            if (updatedRows > 0) {
                return formatAccountNumber(prefix, nextValue);
            }

            try {
                Thread.sleep(10 + attempts * 5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new DataValidationException("Interrupted while generating account number");
            }
        }

        log.error("Failed to generate account number after {} attempts for type: {}", MAX_RETRY_ATTEMPTS, accountType);
        return null;
    }

    private String formatAccountNumber(String prefix, long sequenceValue) {
        String numberPart = String.valueOf(sequenceValue);
        int paddingLength = ACCOUNT_NUMBER_LENGTH - prefix.length();

        if (numberPart.length() > paddingLength) {
            throw new DataValidationException("Sequence value too large for account number format");
        }

        return prefix + String.format("%0" + paddingLength + "d", sequenceValue);
    }

    private void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new DataValidationException("Account number cannot be null or empty");
        }

        if (accountNumber.length() < 12 || accountNumber.length() > 20) {
            throw new DataValidationException("Account number length must be between 12 and 20 characters");
        }

        if (!accountNumber.matches("\\d+")) {
            throw new DataValidationException("Account number must contain only digits");
        }
    }
}