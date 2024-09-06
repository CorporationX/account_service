package faang.school.accountservice.scheduler;

import faang.school.accountservice.service.account.InterestCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculationInterest {

    private final InterestCalculationService interestCalculationService;

    @Scheduled(cron = "${scheduler.calculation-of-interest.cron}")
    public void performDailyInterestCalculation() {
        interestCalculationService.calculateInterest();
    }
}
