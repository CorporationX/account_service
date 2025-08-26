package faang.school.accountservice.service;


import faang.school.accountservice.dto.NotificationMessage;
import faang.school.accountservice.entity.account.Request;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncRequestProcessor {
    private final RequestRepository requestRepository;
    private final RequestService requestService;
    private final NotificationProducer notificationProducer;

    @Async("asyncExecutor")
    public Future<Void> sendNotification(UUID idpToken) {
        try {
            Optional<Request> request = requestRepository.findById(idpToken);
            if (request.isPresent()) {
                NotificationMessage message = new NotificationMessage(
                        idpToken,
                        request.get().getUserId(),
                        "Request processed",
                        request.get().getStatus()
                );
                notificationProducer.sendNotification(message);
                requestService.updateStatus(idpToken, RequestStatus.PROCESSING);
                log.info("Notification sent and status updated for request: {}", idpToken);
            } else {
                log.warn("Request not found for notification: {}", idpToken);
            }
        } catch (Exception e) {
            log.error("Error sending notification for request: {}", idpToken, e);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Scheduled(fixedDelay = 60000)
    public void processPendingRequests() {
        List<Request> pendingRequests = requestRepository.findByStatus(RequestStatus.PENDING);
        log.info("Processing {} pending requests", pendingRequests.size());
        for (Request request : pendingRequests) {
            try {
                UUID idpToken = request.getIdpToken();
                log.debug("Processing request: {}", idpToken);
                Future<Void> future = sendNotification(idpToken);
                future.get();
                log.debug("Successfully processed request: {}", idpToken);
            } catch (InterruptedException e) {
                log.error("Task interrupted while processing request: {}", request.getIdpToken(), e);
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                log.error("Error executing task for request: {}", request.getIdpToken(), e);
            }
        }
        log.info("Completed processing pending requests batch");
    }
}