package faang.school.accountservice.scheduler;

import faang.school.accountservice.service.savings_account.SavingsAccountAccrualService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CalculateAccrualsScheduler {
    private final SavingsAccountAccrualService service;

    @Scheduled(cron = "${app.scheduler.savings_account.accrual}")
    public void startEvent() {
        service.makeAccruals();
    }
}
