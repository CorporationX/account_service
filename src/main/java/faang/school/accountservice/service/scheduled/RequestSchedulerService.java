package faang.school.accountservice.service.scheduled;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.exception.RequestSchedulerServiceException;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.request.RequestExecutorService;
import faang.school.accountservice.service.request.executor.BusinessProcessExecutorProvider;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static faang.school.accountservice.messages.ErrorMessages.INIT_ERROR;
import static faang.school.accountservice.messages.ErrorMessages.EXECUTOR_NOT_FOUND;
import static faang.school.accountservice.messages.ErrorMessages.EXECUTION_FAILED;
import static faang.school.accountservice.messages.ErrorMessages.PROCESS_ERROR;
import static faang.school.accountservice.messages.ErrorMessages.SHUTDOWN_ERROR;
import static faang.school.accountservice.messages.ErrorMessages.EXECUTOR_SHUTDOWN_INTERRUPTED;


@Slf4j
@Service
@RequiredArgsConstructor
public class RequestSchedulerService {
    private static final String EXECUTOR_TIMEOUT_WARNING = "Executor did not terminate in time, forcing shutdown";

    private final RequestRepository requestRepository;
    private final RequestExecutorService requestExecutorService;
    private final List<BusinessProcessExecutorProvider> executorProviders;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private Map<String, ThreadPoolExecutor> executorsByType;

    @Value("${app.request.scheduler.interval}")
    private long schedulerInterval;

    @PostConstruct
    public void init() {
        try {
            executorsByType = executorProviders.stream()
                    .collect(Collectors.toMap(
                            BusinessProcessExecutorProvider::getSupportedType,
                            BusinessProcessExecutorProvider::getExecutor
                    ));
            scheduler.scheduleAtFixedRate(this::processScheduledRequests, 0, schedulerInterval, TimeUnit.MILLISECONDS);
            log.info("RequestSchedulerService initialized successfully.");
        } catch (Exception e) {
            log.error(INIT_ERROR, e);
            throw new RequestSchedulerServiceException(INIT_ERROR);
        }
    }

    public void processScheduledRequests() {
        try {
            List<Request> scheduledRequests = requestRepository
                    .findAllByStatusAndScheduledAtBefore(RequestStatus.IN_PROGRESS, LocalDateTime.now());

            for (Request request : scheduledRequests) {
                ThreadPoolExecutor executor = executorsByType.get(request.getType());

                if (executor == null) {
                    String errorMsg = EXECUTOR_NOT_FOUND + request.getType();
                    log.warn(errorMsg);
                    continue;
                }

                executor.submit(() -> {
                    try {
                        requestExecutorService.execute(request.getIdempotencyToken());
                    } catch (Exception e) {
                        String errorMsg = String.format(EXECUTION_FAILED, request.getIdempotencyToken(), e.getMessage());
                        log.error(errorMsg, e);
                        throw new RuntimeException(errorMsg);
                    }
                });
            }
        } catch (Exception e) {
            log.error(PROCESS_ERROR, e);
            throw new RequestSchedulerServiceException(PROCESS_ERROR);
        }
    }

    @PreDestroy
    public void shutdown() {
        try {
            log.info("Shutting down scheduler and executors");
            scheduler.shutdownNow();

            for (ThreadPoolExecutor executor : executorsByType.values()) {
                executor.shutdownNow();
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        log.warn(EXECUTOR_TIMEOUT_WARNING);
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                    log.error(EXECUTOR_SHUTDOWN_INTERRUPTED, e);
                    throw new RequestSchedulerServiceException(EXECUTOR_SHUTDOWN_INTERRUPTED);
                }
            }
        } catch (Exception e) {
            log.error(SHUTDOWN_ERROR, e);
            throw new RequestSchedulerServiceException(SHUTDOWN_ERROR);
        }
    }
}
