package faang.school.accountservice.service;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.repository.RequestRepository;
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
    private final RequestRepository requestRepository;
    private final RequestExecutorService requestExecutorService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(this::processScheduledRequests,
                0, 500, TimeUnit.MILLISECONDS);
    }

    private void processScheduledRequests() {
        LocalDateTime now = LocalDateTime.now();
        List<Request> requests = requestRepository
                .findAllByStatusAndScheduledAtBefore(RequestStatus.PENDING, now);
        for (Request request : requests) {
            requestExecutorService.executeRequest(request.getId());
        }
    }
}
