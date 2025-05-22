package faang.school.accountservice.background_worker;

import com.github.javafaker.Faker;
import faang.school.accountservice.enums.RequestType;
import faang.school.accountservice.events.RequestEventEvent;
import faang.school.accountservice.exception.TestDataAccessException;
import faang.school.accountservice.publisher.RequestEventsPublisher;
import faang.school.accountservice.service.RequestEventService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestEventsOutboxProcessorTest {

    public static final int PAUSE_TIMEOUT_MS = 1000;
    public static final int REQUEST_EVENTS_BATCH_SIZE = 100;

    @Mock
    private RequestEventService requestEventService;
    @Mock
    private RequestEventsPublisher requestEventsPublisher;

    @Captor
    ArgumentCaptor<List<UUID>> idsCaptor;

    private static final Faker faker = new Faker();

    private ExecutorService testExecutor;
    private List<RequestEventEvent> testEvents;

    private RequestEventsOutboxProcessor processor;

    @BeforeEach
    public void setUp() {
        processor = new RequestEventsOutboxProcessor(requestEventService, requestEventsPublisher,
                Executors.newSingleThreadExecutor());

        testEvents = List.of(getTestRequestEventEvent(), getTestRequestEventEvent());
        testExecutor = Executors.newSingleThreadExecutor();

        ReflectionTestUtils.setField(processor, "pauseTimeoutMs", PAUSE_TIMEOUT_MS);
        ReflectionTestUtils.setField(processor, "requestEventsBatchSize", REQUEST_EVENTS_BATCH_SIZE);
    }

    @AfterEach
    public void tearDown() {
        processor.destroy();
        testExecutor.shutdownNow();
    }

    @Test
    public void shouldFetchAndProcessEvents() throws Exception {
        // Arrange
        when(requestEventService.getEventsSortedByCreationDate(anyInt())).thenReturn(testEvents).thenReturn(List.of());

        // Act
        startProcessor();
        TimeUnit.MILLISECONDS.sleep(500);

        // Assert
        testEvents.forEach(event -> verify(requestEventsPublisher).publish(event));

        verify(requestEventService).deleteAllEventsByIds(idsCaptor.capture());

        var deletedIds = idsCaptor.getValue();
        assertEquals(2, deletedIds.size());
        testEvents.forEach(event -> assertTrue(deletedIds.contains(event.id())));
    }

    @Test
    public void shouldWaitForSignalWhenNoEvents() throws Exception {
        // Arrange
        when(requestEventService.getEventsSortedByCreationDate(anyInt())).thenReturn(List.of());

        // Act
        startProcessor();
        TimeUnit.MILLISECONDS.sleep(300);

        // Assert
        verify(requestEventsPublisher, never()).publish(any());

        var oneMoreEvent = getTestRequestEventEvent();
        when(requestEventService.getEventsSortedByCreationDate(anyInt()))
                .thenReturn(List.of(oneMoreEvent))
                .thenReturn(List.of());

        processor.newRequestEventsAdded();

        TimeUnit.MILLISECONDS.sleep(PAUSE_TIMEOUT_MS + 200);

        verify(requestEventsPublisher).publish(oneMoreEvent);
    }

    @Test
    public void shouldHandleExceptionDuringFetching() throws Exception {
        // Arrange
        when(requestEventService.getEventsSortedByCreationDate(anyInt()))
                .thenThrow(new TestDataAccessException());

        // Act
        startProcessor();
        processor.newRequestEventsAdded();
        TimeUnit.MILLISECONDS.sleep(PAUSE_TIMEOUT_MS + 200);

        // Assert
        verify(requestEventService, atLeast(1)).getEventsSortedByCreationDate(anyInt());
        verify(requestEventsPublisher, never()).publish(any());
    }

    @Test
    public void shouldSkipFailedEventsAndContinueProcessing() throws Exception {
        // Arrange
        when(requestEventService.getEventsSortedByCreationDate(anyInt())).thenReturn(testEvents).thenReturn(List.of());
        doThrow(new RuntimeException("Test exception")).when(requestEventsPublisher).publish(testEvents.get(0));

        // Act
        startProcessor();
        TimeUnit.MILLISECONDS.sleep(500);

        // Assert
        testEvents.forEach(event -> verify(requestEventsPublisher).publish(event));
        verify(requestEventService).deleteAllEventsByIds(idsCaptor.capture());

        var deletedIds = idsCaptor.getValue();
        assertFalse(deletedIds.contains(testEvents.get(0).id()));
        assertTrue(deletedIds.contains(testEvents.get(1).id()));
        assertEquals(1, deletedIds.size());
    }

    @Test
    public void shouldHandleExceptionDuringDeletion() throws Exception {
        // Arrange
        when(requestEventService.getEventsSortedByCreationDate(anyInt())).thenReturn(testEvents).thenReturn(List.of());
        doThrow(new TestDataAccessException()).when(requestEventService).deleteAllEventsByIds(anyList());

        // Act
        startProcessor();
        TimeUnit.MILLISECONDS.sleep(500);

        // Assert
        testEvents.forEach(event -> verify(requestEventsPublisher).publish(event));
        verify(requestEventService).deleteAllEventsByIds(argThat(list ->
                list.size() == testEvents.size()
                        && testEvents.stream().allMatch(event -> list.contains(event.id()))));
        verify(requestEventService, atLeast(2)).getEventsSortedByCreationDate(anyInt());
    }

    private void startProcessor() {
        CompletableFuture.runAsync(() -> processor.init(), testExecutor);
    }

    private static RequestEventEvent getTestRequestEventEvent() {
        RequestType[] statuses = RequestType.values();
        var randomRequestType = statuses[faker.random().nextInt(statuses.length)];

        return RequestEventEvent.builder()
                .id(UUID.randomUUID())
                .requestType(randomRequestType)
                .body(new HashMap<>())
                .build();
    }
}