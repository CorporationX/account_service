package faang.school.accountservice.scheduler;

import faang.school.accountservice.entity.AccountType;
import faang.school.accountservice.service.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {

    @Value("${account.number.batch.size}")
    private int batchSize;

    private final FreeAccountNumberService freeAccountNumberService;

    @Scheduled(cron = "0 0 0 * * *")
    public void generateDebit() {
        freeAccountNumberService.generateAccountNumbers(AccountType.DEBIT, batchSize);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateCredit() {
        freeAccountNumberService.generateAccountNumbers(AccountType.CREDIT,batchSize);
    }
}
