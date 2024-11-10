package faang.school.accountservice.scheduler;

import faang.school.accountservice.service.ReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ReserveScheduler {
    private final ReserveService reserveService;

    @Scheduled(cron = "${reserve-service.canceling-out-of-date-reserves.cron}")
    public void cancelOutOfDateReserves() {
        reserveService.cancelOutOfDateReserves();
    }
}
