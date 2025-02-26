package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.account.AccountNumberSequenceRepository;
import faang.school.accountservice.repository.account.FreeAccountNumberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumberServiceImpl implements FreeAccountNumberService {

    private final FreeAccountNumberRepository freeAccountNumberRepository;
    private final AccountNumberSequenceRepository accountNumberSequenceRepository;

    @Transactional
    @Override
    public void generateAndSaveFreeAccountNumbers(String type, int count) {
        for (int i = 0; i < count; i++) {
            String accountNumber = generateFreeAccountNumber(type);
            freeAccountNumberRepository.saveFreeAccountNumber(type, accountNumber);
        }
    }

    @Transactional
    @Override
    public String getFreeAccountNumber(String accountType, Consumer<String> accountCreation) {
        Optional<String> accountNumber = freeAccountNumberRepository.deleteAndReturnFreeAccountNumber(accountType);
        if (accountNumber.isPresent()) {
            accountCreation.accept(accountNumber.get());
            return accountNumber.get();
        } else {
            String newAccountNumber = generateFreeAccountNumber(accountType);
            accountCreation.accept(newAccountNumber);
            return newAccountNumber;
        }
    }

    @Override
    public long countByAccountType(String accountType) {
        return freeAccountNumberRepository.countByAccountType(accountType);
    }

    private String generateFreeAccountNumber(String type) {
        String code = AccountType.valueOf(type).getValue();
        Optional<Long> currentValueOpt = accountNumberSequenceRepository.incrementSequence(type, 0L);
        if (currentValueOpt.isEmpty()) {
            accountNumberSequenceRepository.createSequence(type);
            currentValueOpt = accountNumberSequenceRepository.incrementSequence(type, 0L);
        }
        long currentValue = currentValueOpt.orElseThrow(() -> new RuntimeException("Failed to generate account number"));
        return code + currentValue;
    }
}
