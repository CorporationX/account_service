package faang.school.accountservice.scheduler;

import faang.school.accountservice.service.BalanceService;
import faang.school.accountservice.service.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
@Component
public class CalculateAccrualsScheduler {
    private final SavingsAccountService savingsAccountService;
    private final BalanceService balanceService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Scheduled(cron = "${app.scheduler.savings_account.accrual}")
    public void startEvent() {
        savingsAccountService.makeAccruals();
    }
}
