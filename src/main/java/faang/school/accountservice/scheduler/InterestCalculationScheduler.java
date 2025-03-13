package faang.school.accountservice.scheduler;

import faang.school.accountservice.service.InterestCalculationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterestCalculationScheduler {
    private static final Logger logger = LoggerFactory.getLogger(InterestCalculationScheduler.class);
    private final InterestCalculationService interestCalculationService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void runDailyInterestCalculation() {
        logger.info("Starting daily interest calculation");
        interestCalculationService.calculateInterest();
        logger.info("Daily interest calculation completed");
    }
}