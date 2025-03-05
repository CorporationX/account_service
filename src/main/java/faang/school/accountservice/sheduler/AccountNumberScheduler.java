package faang.school.accountservice.sheduler;

import faang.school.accountservice.model.account.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {

    @Value("${account.number.batch.size}")
    private int batchSize;

    private final FreeAccountNumberService freeAccountNumberService;

    @Scheduled(cron = "0 0 0 * * *")
    public void generateDebitAccountNumber() {
        freeAccountNumberService.generateAccountNumbers(AccountType.CREDIT, batchSize);
    }
}
