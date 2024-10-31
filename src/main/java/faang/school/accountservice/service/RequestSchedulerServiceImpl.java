package faang.school.accountservice.service;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestHandlerType;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.repository.RequestJpaRepository;
import faang.school.accountservice.repository.RequestTaskRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RequestSchedulerServiceImpl implements RequestSchedulerService {
    private final ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(5);
    private final RequestJpaRepository requestRepository;
    private final RequestTaskRepository requestTaskRepository;
    private Map<RequestHandlerType, ThreadPoolExecutor> threadPoolExecutors;

    @PostConstruct
    public void init() {
        scheduledExecutorService
                .scheduleAtFixedRate(this::processScheduledRequests, 0, 500, TimeUnit.MILLISECONDS);
    }
    private void processScheduledRequests() {
        List<Request> requests = requestRepository.findAllByStatus(RequestStatus.IN_PROGRESS);
        for (Request request : requests) {
            if (request.getScheduledAt().isBefore(LocalDateTime.now())) {
                List<RequestTask> requestTasks = request.getRequestTasks();
                for (RequestTask task : requestTasks) {
                    RequestHandlerType handlerType = task.getHandler();
                    ThreadPoolExecutor executor = threadPoolExecutors.get(handlerType);

                    if (executor != null) {
                        executor.submit(() -> {
                            executeRequest(task.getRequest());
                            task.setStatus(RequestStatus.COMPLETED);
                            requestTaskRepository.save(task.setStatus(RequestStatus.COMPLETED));
                            // Сохраняем изменения в базе данных
                            // Здесь вам может понадобиться использовать requestTaskRepository для сохранения изменений
                        });
                    }
                }
            }
        }
    }


    private void executeRequest(Request request) {
        // Реализация логики выполнения запроса
    }
}
