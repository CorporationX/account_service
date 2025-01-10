package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {
    private final FreeAccountNumbersService freeAccountNumbersService;
    @Value("${accounts.generated.amount}")
    private int batchSize;

    @Scheduled(cron = "${cron.expression.every-midnight}")
    @Async
    public void generateCreditAccountNumber() {
        freeAccountNumbersService.generateFreeAccountNumber(AccountType.CREDIT, batchSize);
        freeAccountNumbersService.generateFreeAccountNumber(AccountType.DEBIT, batchSize);
        freeAccountNumbersService.generateFreeAccountNumber(AccountType.DEPOSIT, batchSize);
        freeAccountNumbersService.generateFreeAccountNumber(AccountType.CURRENCY, batchSize);
        freeAccountNumbersService.generateFreeAccountNumber(AccountType.CURRENT, batchSize);
        freeAccountNumbersService.generateFreeAccountNumber(AccountType.SAVINGS, batchSize);
        freeAccountNumbersService.generateFreeAccountNumber(AccountType.INVESTMENT, batchSize);
    }
}
