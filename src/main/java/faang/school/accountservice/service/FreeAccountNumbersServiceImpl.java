package faang.school.accountservice.service;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersServiceImpl implements FreeAccountNumbersService {

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    @Override
    public String generateNumberByType(AccountType type) {
        return freeAccountNumbersRepository.getFreeAccountNumberByType(type.name())
                .orElseGet(() -> {
                    Long number = accountNumbersSequenceRepository.incrementAndGet(type.name());
                    return accountNumberGenerator.generateAccountNumber(type, number);
                });
    }

    @Override
    @Transactional
    public void generateNumberAndExecute(AccountType type, Consumer<FreeAccountNumber> operation) {
        String accountNumber = generateNumberByType(type);
        FreeAccountNumberId id = FreeAccountNumberId.builder()
                .accountNumber(accountNumber)
                .type(type)
                .build();
        FreeAccountNumber accountNumberEntity = FreeAccountNumber.builder()
                .id(id)
                .build();
        operation.accept(accountNumberEntity);
    }

    @Override
    public void saveNumber(AccountType type, String accountNumber) {
        FreeAccountNumber freeAccountNumber = FreeAccountNumber.builder()
                .id(new FreeAccountNumberId(type, accountNumber))
                .build();
        freeAccountNumbersRepository.save(freeAccountNumber);
    }

    @Override
    @Transactional
    public void generateNumbers(AccountType type, int count) {
        for (int i = 0; i < count; i++) {
            Long number = accountNumbersSequenceRepository.incrementAndGet(type.name());
            String accountNumber = accountNumberGenerator.generateAccountNumber(type, number);
            saveNumber(type, accountNumber);
        }
    }

    @Override
    @Transactional
    public void ensureMinimumNumbers(AccountType type, int requiredCount) {
        long currentCount = freeAccountNumbersRepository.countByIdType(type);
        int numbersToGenerate = requiredCount - (int) currentCount;
        if (numbersToGenerate > 0) {
            generateNumbers(type, numbersToGenerate);
        }
    }
}
