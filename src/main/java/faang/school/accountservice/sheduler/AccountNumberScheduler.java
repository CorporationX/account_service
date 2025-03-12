package faang.school.accountservice.sheduler;

import faang.school.accountservice.model.account.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {

    @Value("${account.number.batch.size}")
    private int batchSize;

    private final FreeAccountNumberService freeAccountNumberService;

    @Scheduled(cron = "${config.schedule-daily-generate-number-cron}")
    public void generateDebitAccountNumber() {
        for (AccountType accountType : AccountType.values()) {
            freeAccountNumberService.generateAccountNumbers(accountType, batchSize);
        }
    }
}
