package faang.school.accountservice.service.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import faang.school.accountservice.entity.account.AccountNumbersSequence;
import faang.school.accountservice.entity.account.FreeAccountNumber;
import faang.school.accountservice.enums.account.AccountEnum;
import faang.school.accountservice.repository.account.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.account.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;


    @Transactional
    public void processAccountNumber(AccountEnum accountType, Consumer<String> action) {
        String accountNumber = freeAccountNumbersRepository.findAndRemoveFreeAccountNumber(accountType);

        if (accountNumber == null) {
            accountNumber = generateNewAccountNumber(accountType);
        }

        if (accountNumber != null) {
            action.accept(accountNumber);
        } else {
            throw new IllegalStateException("Failed to generate or retrieve an account number");
        }
    }

    private String generateNewAccountNumber(AccountEnum accountType) {
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findById(accountType)
            .orElseGet(() -> createCounterForAccountType(accountType));

        boolean hasIncremented = accountNumbersSequenceRepository.incrementCounterForAccountType(accountType, sequence.getCurrentCounter());

        if (hasIncremented) {
            String newAccountNumber = accountType.getPrefix() + String.format("%12d", sequence.getCurrentCounter() + 1);
            createFreeAccountNumber(accountType, newAccountNumber);
            return newAccountNumber;
        }
        return null;
    }

    private AccountNumbersSequence createCounterForAccountType(AccountEnum accountType) {
        AccountNumbersSequence sequence = new AccountNumbersSequence();
        sequence.setAccountType(accountType);
        sequence.setCurrentCounter(0L);
        return accountNumbersSequenceRepository.save(sequence);
    }

    private FreeAccountNumber createFreeAccountNumber(AccountEnum accountType, String freeAccountNumber) {
        FreeAccountNumber entity = new FreeAccountNumber();
        entity.setAccountType(accountType);
        entity.setFreeAccountNumber(freeAccountNumber);
        return freeAccountNumbersRepository.save(entity);
    }
}
