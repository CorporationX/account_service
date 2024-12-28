package faang.school.accountservice.scheduler;

import faang.school.accountservice.service.request.ScheduledRequestExecutorService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestSchedulerService {

    private static final int SCHEDULING_RATE = 500;

    private final ScheduledRequestExecutorService scheduledExecutorService;

    @PostConstruct
    @Scheduled(fixedRate = SCHEDULING_RATE)
    public void executeRequests() {
        scheduledExecutorService.execute();
    }
}
