package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.AccountNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {

    @Value(value = "${spring.data.batch-size}")
    private  long batchSize;
    private final AccountNumberService service;

    @Scheduled(cron = "0 */20 * * * *")
    public void generateAccountNumber() {
        for(AccountType type: AccountType.values()){
            service.generateAccountNumber(type, batchSize);
        }
    }
}
