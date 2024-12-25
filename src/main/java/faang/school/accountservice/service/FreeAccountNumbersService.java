package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.properties.AccountTypeIdentityProperties;
import faang.school.accountservice.properties.AccountTypeLengthProperties;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.validator.FreeAccountNumberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        log.info("Start generating free account number for account type: {}", accountType);
        AccountNumberSequence sequence =
                accountNumbersSequenceRepository.incrementCounter(accountType.name());
        long numberSequence = sequence.getCurrentSequenceValue();
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
        log.info("Finished generating free account number for account type: {}", accountType);
    }


    @Transactional
    public String getFreeAccountNumber(AccountType accountType) {
        log.info("Start getting free account number for account type: {}", accountType);
        FreeAccountNumber freeAccountNumber =
                freeAccountNumbersRepository.retrieveFreeAccountNumber(accountType.name());

        if (freeAccountNumber == null) {
            log.info("No free account number found for account type: {}.Generating new...", accountType);
            generateFreeAccountNumber(accountType);
            freeAccountNumber = freeAccountNumbersRepository.retrieveFreeAccountNumber(accountType.name());
        }
        log.info("Finished getting free account number for account type: {}", accountType);
        return freeAccountNumber.getAccountNumber();
    }

//    @Transactional(isolation = Isolation.SERIALIZABLE)
//    public String getFreeAccountNumber(AccountType accountType) {
//        log.info("Start getting free account number for account type: {}", accountType);
//        FreeAccountNumber freeAccountNumber =
//                freeAccountNumbersRepository.getFirstByAccountType(accountType);
//
//        if (freeAccountNumber == null) {
//            log.info("No free account number found for account type: {}.Generating new...", accountType);
//            generateFreeAccountNumber(accountType);
//            freeAccountNumber = freeAccountNumbersRepository.getFirstByAccountType(accountType);
//        }
//        freeAccountNumbersRepository.deleteByAccountNumber(freeAccountNumber.getAccountNumber());
//        log.info("Finished getting free account number for account type: {}", accountType);
//        return freeAccountNumber.getAccountNumber();
//    }

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
        String characterForBuilding = "0";
        StringBuilder accountNumber = new StringBuilder();
        accountNumber.append(accountTypeIdentity);

        accountNumber.append((characterForBuilding).
                repeat(Math.max(0, (numberLength - String.valueOf(accountTypeIdentity).length()))));

        accountNumber.replace(accountNumber.length() - String.valueOf(uniqueNumber).length(),
                accountNumber.length(), String.valueOf(uniqueNumber));
        return accountNumber.toString();
    }
}