package faang.school.accountservice.sheduler;

import faang.school.accountservice.config.account.FreeAccountNumbersConfig;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.account.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class FreeAccountNumbersScheduler {
    private final FreeAccountNumberService freeAccountNumberService;
    private final FreeAccountNumbersConfig freeAccountNumbersConfig;

    @Scheduled(cron = "${free-account-generation.cron.expression}")
    public void generateFreeAccountNumbers() {
        var accountLength = freeAccountNumbersConfig.getAccountNumberLength();
        var accountNumberCount = freeAccountNumbersConfig.getAccountNumberCount();
        AccountType[] accountTypes = AccountType.values();

        for (AccountType accountType : accountTypes) {
            freeAccountNumberService.generateFreeAccountNumbersWithBatchSize(accountType, accountLength, accountNumberCount);
        }
    }
}
