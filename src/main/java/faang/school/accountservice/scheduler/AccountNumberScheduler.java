package faang.school.accountservice.scheduler;

import faang.school.accountservice.entity.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler{

    private final FreeAccountNumbersService freeAccountNumbersService;

    @Value("${account.number.bucket.size}")
    private long bucketSize;

    @Scheduled(cron = "0 * * * * *")
    public void generateDebitAcc(){
        freeAccountNumbersService.generateAccountNumber(AccountType.DEBIT, bucketSize);
    }

    @Scheduled(cron = "0 * * * * *")
    public void generateCreditAcc(){
        freeAccountNumbersService.generateAccountNumber(AccountType.DEPO, bucketSize);
    }
}

