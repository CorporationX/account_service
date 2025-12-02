package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.number.FreeAccountNumbersServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {

    @Value("${account.number.batch.size}")
    private int batchSize;

    private final FreeAccountNumbersServiceImpl freeAccountNumbersService;

    @Scheduled(cron = "0 0 0 * * *")
    public void generateDebitAccountNumbers() {
        freeAccountNumbersService.generateAccountNumbers(AccountType.CURRENT, batchSize);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateCreditAccountNumbers() {
        freeAccountNumbersService.generateAccountNumbers(AccountType.CREDIT, batchSize);
    }
}
