package faang.school.accountservice.scheduler;

import faang.school.accountservice.service.InterestCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterestScheduler {
    private final InterestCalculationService interestCalculationService;

    @Scheduled(cron = "${account.interest.scheduler.cron}")
    public void runDailyInterestCalculation() {
        interestCalculationService.calculateDailyInterest();
    }
}

