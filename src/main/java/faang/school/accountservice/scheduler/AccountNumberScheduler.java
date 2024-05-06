package faang.school.accountservice.scheduler;

import faang.school.accountservice.model.account.numbers.AccountType;
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

    private final FreeAccountNumbersService freeAccountNumbersService;

    @Scheduled(cron = "0 0 0 * * *")
    public void generateDebit(){
        freeAccountNumbersService.generateAccountNumbers(AccountType.DEBIT, batchSize);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateCredit(){
        freeAccountNumbersService.generateAccountNumbers(AccountType.CREDIT, batchSize);
    }
}
