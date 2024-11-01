package faang.school.accountservice.scheduler.account;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import faang.school.accountservice.enums.account.AccountEnum;
import faang.school.accountservice.service.account.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountNumbersScheduler {

    @Value("${account.batch.size}")
    private int batchSize;

    private final FreeAccountNumbersService freeAccountNumbersService;
    
    @Scheduled(cron = "0 0 0 * * *")
    public void generateDebit() {
        freeAccountNumbersService.generateAccountNumbers(AccountEnum.DEBIT, batchSize);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateSavings() {
        freeAccountNumbersService.generateAccountNumbers(AccountEnum.SAVINGS, batchSize);
    }
}