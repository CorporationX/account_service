package faang.school.accountservice.scheduler;

import faang.school.accountservice.service.account.AccountNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberPoolScheduler {

    private final AccountNumberService accountNumberService;

    @Value("${account.pool.min}")
    private int minAccountNumbers;

    @Scheduled(cron = "${scheduler.account-generator.cron}")
    private void populateAccountNumbers() {
        int count = accountNumberService.getAccountNumberPoolSize();
        if (count < minAccountNumbers) {
            accountNumberService.populateAccountNumbers(minAccountNumbers - count);
        }
    }
}
