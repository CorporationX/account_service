package faang.school.accountservice.scheduler.account;

import faang.school.accountservice.service.account.SavingAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SavingAccountScheduler {
    private final SavingAccountService savingAccountService;

    @Scheduled(cron = "${task.savings-account.cron}")
    public void paySavingsAccountInterest() {
        savingAccountService.payOffInterests();
    }
}
