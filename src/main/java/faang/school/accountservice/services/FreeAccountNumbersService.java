package faang.school.accountservice.services;

import faang.school.accountservice.model.AccountNumbersSequence;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Log4j2
@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Transactional
    public void createNewFreeAccountNumber(String type) {
        Integer fourDigits = getValueForAccountTypeOrThrowException(type);
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findByAccountType(type);
        if (sequence == null) {
            log.error("No sequence found for account type: {}", type);
            throw new IllegalArgumentException("No sequence found for account type: " + type);
        }

        Long lastDigits = sequence.getCurrent();
        Long newAccountNumber = Long.valueOf(fourDigits.toString() + String.format("%011d", lastDigits));
        freeAccountNumbersRepository.saveNewFreeAccountNumber(type, newAccountNumber);
        accountNumbersSequenceRepository.incrementCounter(type, lastDigits);
    }



    @Transactional
    public Long getFreeAccountNumberWithTransaction(String accountType, Consumer<Long> action) {
        Long accountNumber = freeAccountNumbersRepository.getAndRemoveFirstFreeAccountNumber(accountType);

        if (accountNumber == null) {
            createNewFreeAccountNumber(accountType);
            accountNumber = freeAccountNumbersRepository.getAndRemoveFirstFreeAccountNumber(accountType);

            if (accountNumber == null) {
                throw new RuntimeException("No free account numbers available");
            }
        }

        try {
            action.accept(accountNumber);
            return accountNumber;
        } catch (Exception e) {
            freeAccountNumbersRepository.saveNewFreeAccountNumber(accountType, accountNumber);
            log.error(e);
            throw e;
        }
    }

    public void createNewAccountNumbersSequence(String accountType) {
        if (accountNumbersSequenceRepository.findByAccountType(accountType) == null) {
            accountNumbersSequenceRepository.createAccountTypeCounter(accountType);
        } else {
            log.error("AccountNumbersSequence for account type {} is exist", accountType);
            throw new IllegalArgumentException("AccountNumbersSequence for account type " + accountType + " is exist");
        }
    }

    private Integer getValueForAccountTypeOrThrowException(String type) {
        try {
            AccountType accountType = AccountType.valueOf(type);
            return accountType.getPrefix();
        } catch (IllegalArgumentException e) {
            log.error("Invalid account type: {}", type, e);
            throw new IllegalArgumentException("Invalid account type: " + type, e);
        }
    }
}