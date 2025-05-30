package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumbersScheduler {

    private final FreeAccountNumbersService freeAccountNumbersService;

    @Value("${account.number.batch.size}")
    private int batchSize;

    @Scheduled(cron = "${scheduler.account-numbers-generation.debit}")
    public void generateDebitNumbers() {
        freeAccountNumbersService.generateAccountNumbers(AccountNumberType.DEBIT, batchSize);
    }

    @Scheduled(cron = "${scheduler.account-numbers-generation.savings}")
    public void generateSavingsNumbers() {
        freeAccountNumbersService.generateAccountNumbers(AccountNumberType.SAVINGS, batchSize);
    }
}
