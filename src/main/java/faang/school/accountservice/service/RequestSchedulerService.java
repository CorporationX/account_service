package faang.school.accountservice.service;

import faang.school.accountservice.config.executor.ExecutorProperties;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.repository.RequestRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class RequestSchedulerService {
    private final ExecutorProperties executorProperties;
    private final RequestExecutorService requestExecutorService;
    private final RequestRepository requestRepository;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private Map<Long, ThreadPoolExecutor> threadPoolExecutors;

    @PostConstruct
    public void init() {
        threadPoolExecutors = Map.of(
                1L, createThreadPoolExecutor(),
                2L, createThreadPoolExecutor(),
                3L, createThreadPoolExecutor(),
                4L, createThreadPoolExecutor(),
                5L, createThreadPoolExecutor(),
                6L, createThreadPoolExecutor()
        );
        scheduledExecutorService.scheduleAtFixedRate(this::processScheduledRequests, 0, 500, TimeUnit.MILLISECONDS);
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
        LocalDateTime now = LocalDateTime.now();
        List<Request> requests = requestRepository.findAllByStatus(RequestStatus.PENDING);
        for (Request request : requests) {
            if (request.getScheduledAt().isBefore(now)) {
                List<RequestTask> tasks = request.getRequestTasks();
                for (RequestTask task : tasks) {
                    Long handlerId = task.getCurrentHandlerStep();
                    ThreadPoolExecutor executor = threadPoolExecutors.get(handlerId);
                    if (executor != null) {
                        executor.execute(() -> requestExecutorService.executeRequest(request.getId()));
                    }
                }
            }
        }
    }
}

