package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {

    @Value("${scheduler.generate.card-numbers.batch-size}")
    private int batchSize;

    private final FreeAccountNumbersServiceImpl freeAccountNumbersService;

    @Scheduled(cron = "${scheduler.cron}")
    public void generateAccountNumberDebitType() {
        freeAccountNumbersService.generateAccountNumbers(AccountType.DEBIT, batchSize);
    }

    @Scheduled(cron = "${scheduler.cron}")
    public void generateAccountNumberCreditType() {
        freeAccountNumbersService.generateAccountNumbers(AccountType.CREDIT, batchSize);
    }

}
