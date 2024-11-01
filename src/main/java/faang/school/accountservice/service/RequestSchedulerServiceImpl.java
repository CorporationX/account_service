package faang.school.accountservice.service;

import faang.school.accountservice.config.executor.ExecutorProperties;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestHandlerType;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.repository.RequestJpaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class RequestSchedulerServiceImpl implements RequestSchedulerService {
    private final ExecutorProperties executorProperties;
    private final RequestExecutorService requestExecutorService;
    private final ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(5);
    private final RequestJpaRepository requestRepository;
    private Map<RequestHandlerType, ThreadPoolExecutor> threadPoolExecutors;

    @PostConstruct
    public void init() {
        threadPoolExecutors = Map.of(
                RequestHandlerType.AMOUNT_HANDLER, createThreadPoolExecutor(),
                RequestHandlerType.ACCOUNT_RECORD_HANDLER, createThreadPoolExecutor(),
                RequestHandlerType.AUDIT_RECORD_HANDLER, createThreadPoolExecutor(),
                RequestHandlerType.BALANCE_RECORD_HANDLER, createThreadPoolExecutor(),
                RequestHandlerType.OPEN_ACCOUNT_NOTIFICATION_HANDLER, createThreadPoolExecutor(),
                RequestHandlerType.CASHBACK_RECORD_HANDLER, createThreadPoolExecutor()
        );
        scheduledExecutorService
                .scheduleAtFixedRate(this::processScheduledRequests, 0, 500, TimeUnit.MILLISECONDS);
    }

    private ThreadPoolExecutor createThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                executorProperties.getCorePoolSize(),
                executorProperties.getMaxPoolSize(),
                executorProperties.getKeepAliveTime(),
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(executorProperties.getQueueCapacity())
        );
    }

    private void processScheduledRequests() {
        List<Request> requests = requestRepository.findAllByStatus(RequestStatus.IN_PROGRESS);
        for (Request request : requests) {
            if (request.getScheduledAt().isBefore(LocalDateTime.now())) {
                requestExecutorService.executeRequest(request.getId());
            }
        }
    }
}
