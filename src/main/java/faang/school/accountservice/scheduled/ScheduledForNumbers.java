package faang.school.accountservice.scheduled;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledForNumbers {

    private final FreeAccountNumbersService freeAccountNumbersService;

    @Value(value = "${scheduled.task.amountGenerateNumbers}")
    private int amountOfNumbersGenerated;

    @Scheduled(cron = "${scheduled.task.cronForGenerateNumbers}")
    private void getNumbers() {
        List<String> numbers = new ArrayList<>();
        for (int i = 0; i < amountOfNumbersGenerated; i++) {
            numbers.add(freeAccountNumbersService.generateNumberByType(AccountType.SAVINGS_ACCOUNT));
        }

        freeAccountNumbersService.saveNumbers(AccountType.SAVINGS_ACCOUNT, numbers);
    }
}
