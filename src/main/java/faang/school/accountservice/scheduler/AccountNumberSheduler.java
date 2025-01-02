package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberSheduler {
    private final FreeAccountNumberService freeAccountNumberService;

    @Value("${account.number.batch.size}")
    private int batchSize;

    @Scheduled(cron = "0 */1 * * * *")
    public void generateAccountNumbers() {
        freeAccountNumberService.generateFreeAccountNumbers(AccountType.DEBIT, batchSize,
                FreeAccountNumberService.DEBIT_ACCOUNT_PATTERN);
        freeAccountNumberService.generateFreeAccountNumbers(AccountType.CREDIT, batchSize,
                FreeAccountNumberService.CREDIT_ACCOUNT_PATTERN);
    }
}
