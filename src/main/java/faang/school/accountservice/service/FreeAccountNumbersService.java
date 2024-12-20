package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.properties.AccountTypeIdentityProperties;
import faang.school.accountservice.properties.AccountTypeLengthProperties;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.validator.FreeAccountNumberValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {

    private final AccountTypeIdentityProperties identityProp;
    private final AccountTypeLengthProperties lengthProp;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumberValidator freeAccountNumberValidator;

    @Transactional
    public void generateFreeAccountNumber(AccountType accountType) {
        long numberSequence = incrementSequence(accountType);
        int accountNumberLength = getLengthByAccountType(accountType);
        int accountTypeIdentity = getNumberIdentityByAccountType(accountType);

        freeAccountNumberValidator.validateNumberSequenceIsNotExceeded(numberSequence,
                accountNumberLength, accountTypeIdentity);
        String accountNumber = buildAccountNumber(accountTypeIdentity,
                numberSequence, accountNumberLength);

        freeAccountNumbersRepository.save(FreeAccountNumber.builder()
                .accountType(accountType)
                .accountNumber(accountNumber)
                .build());
    }

    @Transactional
    public String getFreeAccountNumber(AccountType accountType) {
        FreeAccountNumber freeAccountNumber =
                freeAccountNumbersRepository.getFirstByAccountType(accountType);

        if (freeAccountNumber == null) {
            generateFreeAccountNumber(accountType);
            freeAccountNumber = freeAccountNumbersRepository.getFirstByAccountType(accountType);
        }
        freeAccountNumbersRepository.deleteById(freeAccountNumber.getId());
        return freeAccountNumber.getAccountNumber();
    }

    private Long incrementSequence(AccountType accountType) {
        int increment = 1;
        int numberOfTries = 3;

        try {
            AccountNumberSequence sequence = accountNumbersSequenceRepository.
                    findByAccountType(accountType);
            if (sequence == null) {
                throw new EntityNotFoundException("Sequence not found for account type: " + accountType);
            }
            Long newValue = sequence.getCurrentSequenceValue() + increment;
            sequence.setCurrentSequenceValue(newValue);
            accountNumbersSequenceRepository.save(sequence);
            return newValue;

        } catch (OptimisticLockException e) {
            log.info("Optimistic lock conflict, retrying...",e);
            int counter = 0;
            while (counter < numberOfTries) {
                counter++;
                incrementSequence(accountType);
            }
        }
        throw new InternalError("Failed to increment sequence after retries");
    }

    private int getLengthByAccountType(AccountType accountType) {
        return switch (accountType) {
            case INDIVIDUAL -> lengthProp.getIndividual();
            case LEGAL -> lengthProp.getLegal();
            case SAVINGS -> lengthProp.getSavings();
            case DEBIT -> lengthProp.getDebit();
        };
    }

    private int getNumberIdentityByAccountType(AccountType accountType) {
        return switch (accountType) {
            case INDIVIDUAL -> identityProp.getIndividual();
            case LEGAL -> identityProp.getLegal();
            case SAVINGS -> identityProp.getSavings();
            case DEBIT -> identityProp.getDebit();
        };
    }

    private String buildAccountNumber(int accountTypeIdentity, long uniqueNumber, int numberLength) {
        char characterForBuilding = '0';
        StringBuilder accountNumber = new StringBuilder();
        accountNumber.append(accountTypeIdentity);

        accountNumber.append(String.valueOf(characterForBuilding).
                repeat(Math.max(0, (numberLength - String.valueOf(accountTypeIdentity).length()))));

        accountNumber.replace(accountNumber.length() - String.valueOf(uniqueNumber).length(),
                accountNumber.length(), String.valueOf(uniqueNumber));
        return accountNumber.toString();
    }
}