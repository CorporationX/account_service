package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {

    @Value("${scheduler.batch-size}")
    private int batchSize;

    private final FreeAccountNumbersService freeAccountNumbersService;

    @Scheduled(cron = "${scheduler.cron}")
    public void generateDebit() {
        freeAccountNumbersService.generateAccountNumbers(AccountType.DEBIT, batchSize);
    }

    @Scheduled(cron = "${scheduler.cron}")
    public void generateCredit() {
        freeAccountNumbersService.generateAccountNumbers(AccountType.CREDIT, batchSize);
    }

}
