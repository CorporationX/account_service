package faang.school.accountservice.scheduler;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.RequestExecutorService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RequestSchedulerService {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final RequestRepository requestRepository;
    private final RequestExecutorService executor;

    @PostConstruct
    public void scheduledTasks() {
        scheduler.scheduleAtFixedRate(() -> {
            List<Request> requests = requestRepository.findPendingRequests(RequestStatus.PENDING ,LocalDateTime.now());
            for (Request request : requests) {
                executor.executeRequest(request.getId());
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }
}
