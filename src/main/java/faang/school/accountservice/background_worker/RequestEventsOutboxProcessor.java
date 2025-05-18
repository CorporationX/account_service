package faang.school.accountservice.background_worker;

import faang.school.accountservice.events.RequestEventEvent;
import faang.school.accountservice.publisher.RequestEventsPublisher;
import faang.school.accountservice.service.RequestEventService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestEventsOutboxProcessor {

    private enum Token {INSTANCE}

    public static final int EXECUTOR_FINISH_WAITING_TIMEOUT_SECONDS = 5;

    @Value("${request-events-outbox-processor-pause-timeout_ms}")
    @SuppressWarnings("unused")
    private int pauseTimeoutMs;

    @Value("${request-events-outbox-processor-request-events-batch-size}")
    @SuppressWarnings("unused")
    private int requestEventsBatchSize;

    private final BlockingQueue<Token> signalQueue = new LinkedBlockingQueue<>();
    private final RequestEventService requestEventService;
    private final RequestEventsPublisher requestEventsPublisher;

    private ExecutorService executor;
    private Future<?> processingRequestEventsTask;

    @PostConstruct
    @SuppressWarnings("unused")
    public void init() {
        executor = Executors.newSingleThreadExecutor();
        processingRequestEventsTask = executor.submit(this::processRequestEvents);
    }

    @PreDestroy
    @SuppressWarnings("unused")
    public void destroy() {
        if (executor == null || processingRequestEventsTask == null) {
            return;
        }
        stopProcessing();
    }

    public void newRequestEventsAdded() {
        signalQueue.add(Token.INSTANCE);
    }

    private void stopProcessing() {
        executor.shutdown();
        processingRequestEventsTask.cancel(false);
        newRequestEventsAdded();

        try {
            if (!executor.awaitTermination(EXECUTOR_FINISH_WAITING_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                log.warn("Executor did not terminate in the specified time: {} seconds.",
                        EXECUTOR_FINISH_WAITING_TIMEOUT_SECONDS);
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            log.error("Unexpected interrupted exception on executor destroy: {}", ex.getMessage(), ex);
            executor.shutdownNow();
        }
    }

    private void processRequestEvents() {
        while (!Thread.currentThread().isInterrupted()) {
            signalQueue.clear();

            var eventsToPublish = fetchRequestEvents();
            if (eventsToPublish.isEmpty()) {
                waitForNextEvents();
            } else {
                List<UUID> eventIdsToDelete = new ArrayList<>();
                eventsToPublish.forEach(eventToPublish -> publishEvent(eventToPublish, eventIdsToDelete));

                if (!eventIdsToDelete.isEmpty()) {
                    deleteRequestEventsByIds(eventIdsToDelete);
                }
            }
        }
    }

    private List<RequestEventEvent> fetchRequestEvents() {
        try {
            return requestEventService.getEventsSortedByCreationDate(requestEventsBatchSize);
        } catch (DataAccessException ex) {
            log.error("Cannot retrieve request events from database: {}", ex.getMessage(), ex);
            pause();

            return List.of();
        }
    }

    private void pause() {
        try {
            Thread.sleep(pauseTimeoutMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Unexpected interrupted exception on pause: {}", ex.getMessage(), ex);
        }
    }

    private void publishEvent(RequestEventEvent eventToPublish, List<UUID> eventIdsToDelete) {
        try {
            requestEventsPublisher.publish(eventToPublish);
            eventIdsToDelete.add(eventToPublish.id());
        } catch (Exception ex) {
            log.error("Failed to publish request event with token {} after all retries: {}",
                    eventToPublish.id(), ex.getMessage(), ex);
        }
    }

    private void waitForNextEvents() {
        try {
            signalQueue.take();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Unexpected interrupted exception: {}", ex.getMessage(), ex);
        }
    }

    private void deleteRequestEventsByIds(List<UUID> eventIdsToDelete) {
        try {
            requestEventService.deleteAllEventsByIds(eventIdsToDelete);
        } catch (DataAccessException ex) {
            log.error("Cannot delete some request events from database: {}", ex.getMessage(), ex);
        }
    }
}
