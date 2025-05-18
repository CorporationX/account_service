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

/**
 * Class to process request events in background
 */
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

    /**
     * Notified internal background worker to events have been added
     */
    public void newRequestEventsAdded() {
        signalQueue.add(Token.INSTANCE);
    }

    /**
     * Gracefully stop background thread
     */
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

    /**
     * Process request events.
     */
    private void processRequestEvents() {
        while (!Thread.currentThread().isInterrupted()) {
            signalQueue.clear();

            var eventsToPublish = fetchRequestEvents();
            if (eventsToPublish.isEmpty()) {
                // There are not any events to process -> wait
                waitForNextEvents();
            } else {
                // Process retrieved events and delete processed ones from DB
                List<UUID> eventIdsToDelete = new ArrayList<>();
                eventsToPublish.forEach(eventToPublish -> publishEvent(eventToPublish, eventIdsToDelete));

                if (!eventIdsToDelete.isEmpty()) {
                    deleteRequestEventsByIds(eventIdsToDelete);
                }
            }
        }
    }

    /**
     * Fetch data from DB, pause if fetching has been failed.
     * @return fetched data
     */
    private List<RequestEventEvent> fetchRequestEvents() {
        try {
            return requestEventService.getEventsSortedByCreationDate(requestEventsBatchSize);
        } catch (DataAccessException ex) {
            log.error("Cannot retrieve request events from database: {}", ex.getMessage(), ex);
            pause();

            return List.of();
        }
    }

    /**
     * Just pause pauseTimeoutMs milliseconds
     */
    private void pause() {
        try {
            Thread.sleep(pauseTimeoutMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Unexpected interrupted exception on pause: {}", ex.getMessage(), ex);
        }
    }

    /**
     * Publish event to REDIS by requestEventsPublisher
     * @param eventToPublish event to publish, obviously
     * @param eventIdsToDelete List is contained processed events to delete theirs after processing.
     *                         Method add new event to list after publishing is succeeded
     */
    private void publishEvent(RequestEventEvent eventToPublish, List<UUID> eventIdsToDelete) {
        try {
            requestEventsPublisher.publish(eventToPublish);
            eventIdsToDelete.add(eventToPublish.id());
        } catch (Exception ex) {
            log.error("Failed to publish request event with token {} after all retries: {}",
                    eventToPublish.id(), ex.getMessage(), ex);
        }
    }

    /**
     * Wait for newRequestEventsAdded has been called to start retrieving events by requestEventService
     */
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
