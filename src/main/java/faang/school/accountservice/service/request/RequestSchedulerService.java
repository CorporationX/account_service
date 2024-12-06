package faang.school.accountservice.service.request;

import faang.school.accountservice.config.executor.ExecutorServiceConfig;
import faang.school.accountservice.dto.account.RequestDto;
import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.entity.request.RequestStatus;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.apache.commons.collections4.ListUtils.partition;

@Slf4j
@Service
@EnableRetry
@RequiredArgsConstructor
public class RequestSchedulerService {

    private final RequestExecutorService requestExecutorService;
    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final ExecutorServiceConfig executorConfig;

    private final BlockingQueue<RequestDto> queue = new LinkedBlockingQueue<>();

    public void addLastRequestDto(RequestDto requestDto) {
        queue.offer(requestDto);
    }

    @Retryable(retryFor = {IOException.class, OptimisticLockException.class}, maxAttempts = 1)
    @Scheduled(cron = "${scheduler.requestJob.cron}")
    public void scheduled() {
        RequestDto requestDto = queue.poll();
        if(requestDto == null) {
            log.warn("Нет данных для обработки. Планировщик пропускает выполнение.");
            return;
        }

        log.info("DTO получен и обработан в RequestSchedulerService: {}", requestDto);

        List<Request> requests = requestService.findRequestScheduled();
        List<List<Request>> subLists = partition(requests, 100);

        for (List<Request> requestList : subLists) {
            processRequestList(requestList, requestDto);
        }
    }

    private void processRequestList(List<Request> requestList, RequestDto requestDto) {
        if (!checkStatus(requestDto.getRequestStatus())) {
            log.warn("Invalid status for processing: {}", requestDto.getAccountCreateDto());
            return;
        }
        List<CompletableFuture<Void>> futures = requestList.stream()
                .map(request -> CompletableFuture.runAsync(() -> {
                    try {
                        openingAccount(requestDto);
                        saveRequest(List.of(request));
                    } catch (Exception e) {
                        log.error("Error processing request {}: {}", request, e.getMessage(), e);
                        throw new RuntimeException(e);
                    } finally {
                        saveRequestTaskError(requestDto);
                    }
                }, executorConfig.executorServiceAsync()))
                .toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allFutures.get(60, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            log.error("Timeout while processing request list: {}", requestList, ex);
        } catch (Exception e) {
            log.error("Error joining futures for request list: {}", requestList, e);
        }
    }

    private boolean checkStatus(RequestStatus request) {
        return request.equals(RequestStatus.PENDING);
    }

    private void openingAccount(RequestDto accountCreateDto) {
        try {
            requestExecutorService.openingAccount(accountCreateDto);
        } catch (Exception e) {
            log.error("Error opening account: {}", accountCreateDto, e);
            throw new RuntimeException("Failed to open account", e);
        }

    }

    private void saveRequest(List<Request> requests) {
        try {
            requestService.startProcessCreateAccount(requests);
        } catch (Exception e) {
            log.error("Error saving requests: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save requests", e);
        }
    }
    private void saveRequestTaskError(RequestDto requestDto) {
        try {
            requestTaskService.saveRequestTask(requestDto);
        } catch (Exception e) {
            log.error("Error saving requests: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save requests", e);
        }
    }
}

