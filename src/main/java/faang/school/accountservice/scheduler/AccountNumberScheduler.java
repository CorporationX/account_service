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
    private final FreeAccountNumbersService freeAccountNumbersService;
    @Value("${batchSize}")
    private int batchSize;

    @Scheduled(cron = "${cron.expression.every-midnight}")
    public void generateCreditAccountNumber() {
        freeAccountNumbersService.generateFreeAccountNumber(AccountType.CREDIT, batchSize,
                FreeAccountNumbersService.CREDIT_PATTERN);
    }

    @Scheduled(cron = "${cron.expression.every-midnight}")
    public void generateDebitAccountNumber() {
        freeAccountNumbersService.generateFreeAccountNumber(AccountType.DEBIT, batchSize,
                FreeAccountNumbersService.DEBIT_PATTERN);
    }
}
