package faang.school.accountservice.service.impl;

import faang.school.accountservice.dto.account.FreeAccountNumberDto;
import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.service.FreeAccountNumbersService;
import faang.school.accountservice.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    public void saveNumbers(AccountType type, List<String> numbers) {
        freeAccountNumbersRepository.saveAll(
                numbers.stream()
                        .map(number -> FreeAccountNumber.builder()
                                .id(new FreeAccountNumberId(type, number))
                                .build())
                        .collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public void createFreeAccountNumbers(FreeAccountNumberDto freeAccountNumberDto) {
        generateFreeAccountNumbers(freeAccountNumberDto.getType(), freeAccountNumberDto.getBatchSize());
    }

    private void generateFreeAccountNumbers(AccountType type, int batchSize) {
        AccountNumbersSequence numberPeriod = freeAccountNumbersRepository.incrementByBatchSize(type.name(), batchSize);
        List<FreeAccountNumber> freeAccountNumbers = new ArrayList<>();

        for (long i = numberPeriod.getInitialValue(); i < numberPeriod.getSequenceValue(); i++) {
            String generatedAccountNumber = accountNumberGenerator.generateAccountNumber(type, i);

            freeAccountNumbers.add(
                    FreeAccountNumber.builder()
                            .id(new FreeAccountNumberId(type, generatedAccountNumber))
                            .build()
            );
        }

        freeAccountNumbersRepository.saveAll(freeAccountNumbers);
    }

}
