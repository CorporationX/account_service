package faang.school.accountservice.listener.redis.listeners;

import faang.school.accountservice.dto.listener.pending.OperationMessage;
import faang.school.accountservice.exception.pending.UnknownOperationException;
import faang.school.accountservice.service.pending.PendingOperationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.listener.Topic;

import static faang.school.accountservice.enums.pending.OperationStatus.CANCELLATION;
import static faang.school.accountservice.enums.pending.OperationStatus.CLEARING;
import static faang.school.accountservice.enums.pending.OperationStatus.ERROR;
import static faang.school.accountservice.enums.pending.OperationStatus.PENDING;
import static faang.school.accountservice.enums.pending.OperationStatus.UNKNOWN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PendingOperationRedisListenerTest {
    @Mock
    private Topic topic;

    @Mock
    private PendingOperationService pendingOperationService;

    @InjectMocks
    private PendingOperationRedisListener redisListener;

    private final OperationMessage operationMessage = new OperationMessage();

    @Test
    void testSaveEvent_authorization() {
        operationMessage.setStatus(PENDING);

        redisListener.saveEvent(operationMessage);

        verify(pendingOperationService).authorization(operationMessage);
        verifyNoMoreInteractions(pendingOperationService);
    }

    @Test
    void testSaveEvent_clearing() {
        operationMessage.setStatus(CLEARING);

        redisListener.saveEvent(operationMessage);

        verify(pendingOperationService).clearing(operationMessage);
        verifyNoMoreInteractions(pendingOperationService);
    }

    @Test
    void testSaveEvent_cancellation() {
        operationMessage.setStatus(CANCELLATION);

        redisListener.saveEvent(operationMessage);

        verify(pendingOperationService).cancellation(operationMessage);
        verifyNoMoreInteractions(pendingOperationService);
    }

    @Test
    void testSaveEvent_error() {
        operationMessage.setStatus(ERROR);

        redisListener.saveEvent(operationMessage);

        verify(pendingOperationService).cancellation(operationMessage);
        verifyNoMoreInteractions(pendingOperationService);
    }

    @Test
    void testSaveEvent_unknownTypeException() {
        operationMessage.setStatus(UNKNOWN);

        assertThatThrownBy(() -> redisListener.saveEvent(operationMessage))
                .isInstanceOf(UnknownOperationException.class);
    }

    @Test
    void testEventType_successful() {
        assertThat(redisListener.getEventType()).isEqualTo(OperationMessage.class);
    }

    @Test
    void testHandleException_successful() {
        when(topic.getTopic()).thenReturn("topic");
        assertThatThrownBy(() -> redisListener.handleException(new RuntimeException()))
                .isInstanceOf(RuntimeException.class);
    }
}
