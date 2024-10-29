package faang.school.accountservice.service.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import faang.school.accountservice.entity.account.AccountNumbersSequenceRepository;
import faang.school.accountservice.enums.account.AccountEnum;
import faang.school.accountservice.repository.account.FreeAccountNumbersRepository;

import java.util.function.Consumer;

@Service
public class FreeAccountNumbersService {

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    public FreeAccountNumbersService(FreeAccountNumbersRepository freeAccountNumbersRepository, AccountNumbersSequenceRepository accountNumbersSequenceRepository) {
        this.freeAccountNumbersRepository = freeAccountNumbersRepository;
        this.accountNumbersSequenceRepository = accountNumbersSequenceRepository;
    }

    @Transactional
    public void processAccountNumber(AccountEnum accountType, Consumer<Long> action) {
        Long accountNumber = freeAccountNumbersRepository.findAndRemoveFreeAccountNumber(accountType);
        if (accountNumber == null) {
            Long currentCounter = accountNumbersSequenceRepository.findById(accountType)
                .orElseThrow(() -> new IllegalStateException("Counter not initialized"))
                .getCurrentCounter();
            if (accountNumbersSequenceRepository.incrementCounterIfEquals(accountType, currentCounter) == 1) {
                accountNumber = Long.parseLong(accountType.getPrefix() + String.format("%08d", currentCounter + 1));
                freeAccountNumbersRepository.createFreeAccountNumber(accountType, accountNumber);
            }
        }
        if (accountNumber != null) {
            action.accept(accountNumber);
        } else {
            throw new IllegalStateException("Failed to generate account number");
        }
    }
}
