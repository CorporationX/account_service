package faang.school.accountservice.scheduler;

import faang.school.accountservice.service.InterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterestAccruer {
    private final InterestService interestService;

    @Async("scheduledThreadPool")
    @Scheduled(cron = "${schedule.cron.interest}")
    public void accrueInterest() {
        interestService.accrueInterest();
    }
}
