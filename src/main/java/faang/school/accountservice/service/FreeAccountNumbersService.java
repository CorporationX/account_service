package faang.school.accountservice.service;

import faang.school.accountservice.entiry.AccountNumberSequence;
import faang.school.accountservice.entiry.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.validator.FreeAccountNumberValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumberValidator freeAccountNumberValidator;

    @Transactional
    public void generateFreeAccountNumber(AccountType accountType) {
        AccountNumberSequence numberSequence =
                accountNumbersSequenceRepository.getByAccountType(accountType.toString());
        int accountNumberLength = getLengthByAccountType(accountType);
        int accountTypeIdentity = getNumberIdentityByAccountType(accountType);
        freeAccountNumberValidator.validateNumberSequenceIsNotExceeded(numberSequence,
                accountNumberLength, accountTypeIdentity);

        String accountNumber = buildAccountNumber(accountTypeIdentity,
                numberSequence.getCurrentSequenceValue(), accountNumberLength);

        freeAccountNumbersRepository.save(FreeAccountNumber.builder()
                .accountType(accountType)
                .accountNumber(accountNumber)
                .build());
    }

    @Transactional
    public FreeAccountNumber getFreeAccountNumber(AccountType accountType) {
        FreeAccountNumber freeAccountNumber =
                freeAccountNumbersRepository.getFirstByAccountType(accountType);
        freeAccountNumbersRepository.delete(freeAccountNumber);
        return freeAccountNumber;
    }

    private int getLengthByAccountType(AccountType accountType) {
        return switch (accountType) {
            case INDIVIDUAL -> 12;
            case LEGAL -> 20;
            case SAVINGS, DEBIT -> 16;
        };
    }

    private int getNumberIdentityByAccountType(AccountType accountType) {
        return switch (accountType) {
            case INDIVIDUAL -> 3222;
            case LEGAL -> 4333;
            case SAVINGS -> 5444;
            case DEBIT -> 6555;
        };
    }

    private String buildAccountNumber(long accountTypeIdentity, long uniqueNumber, long numberLength) {
        long quantityOfZeros = numberLength - accountTypeIdentity - uniqueNumber;
        StringBuilder result = new StringBuilder();
        result.append(accountTypeIdentity);

        for (int i = 0; i < quantityOfZeros; i++) {
            result.append("0");
        }
        return result.append(uniqueNumber).toString();
    }
}