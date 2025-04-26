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

    @Value("${account.number.batch.size}")
    private int batchSize;
    private FreeAccountNumbersService freeAccountNumbersService;

    @Scheduled(cron = "0 0 0 * * *")
    public void generatedDebit() {
        freeAccountNumbersService.generatedAccountNumbers(AccountType.DEBIT,batchSize);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generatedCredit() {
        freeAccountNumbersService.generatedAccountNumbers(AccountType.CREDIT,batchSize);
    }
}
