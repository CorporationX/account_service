package faang.school.accountservice.scheduler.free_account_number;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FreeAccountNumberScheduler {

    @Value("${free-account-number.batch-size}")
    private int freeAccountNumberBatchSize;

    private final FreeAccountNumbersService freeAccountNumbersService;

    @Scheduled(cron = "${free-account-number.cron}")
    public void generateFreeSavingsAccountNumbers() {
        freeAccountNumbersService.generateFreeAccountNumbers(AccountType.SAVINGS, freeAccountNumberBatchSize);
    }
}