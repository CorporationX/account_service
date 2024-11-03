package faang.school.accountservice.service;


import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenerateNumbersImpl implements GenerateNumbers {

    private final FreeAccountNumbersService freeAccountNumbersService;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Value(value = "${scheduled.task.amountGenerateNumbers}")
    private int amountGenerateNumbers;

    @Override
    public String prepareNumberForAccount() {
        return freeAccountNumbersRepository.getFreeAccountNumberByType(AccountType.SAVINGS_ACCOUNT.name()).orElseThrow(
                () -> new EntityNotFoundException("Number not found"));
    }

    @Scheduled(cron = "${scheduled.task.cronForGenerateNumbers}")
    private void generateNumbers() {
        List<String> numbers = new ArrayList<>();
        for (int i = 0; i < amountGenerateNumbers; i++) {
            numbers.add(freeAccountNumbersService.generateNumberByType(AccountType.SAVINGS_ACCOUNT));
        }

        freeAccountNumbersService.saveNumbers(AccountType.SAVINGS_ACCOUNT, numbers);
    }
}
