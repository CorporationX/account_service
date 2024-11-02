package faang.school.accountservice.listener.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.listener.pending.OperationMessage;
import faang.school.accountservice.exception.ApiException;
import faang.school.accountservice.exception.pending.UnknownOperationException;
import faang.school.accountservice.service.pending.PendingOperationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.accountservice.enums.pending.OperationStatus.AUTHORIZATION;
import static faang.school.accountservice.enums.pending.OperationStatus.CANCELLATION;
import static faang.school.accountservice.enums.pending.OperationStatus.CLEARING;
import static faang.school.accountservice.enums.pending.OperationStatus.ERROR;
import static faang.school.accountservice.enums.pending.OperationStatus.UNKNOWN;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PendingOperationKafkaListenerTest {
    private static final String MESSAGE = "{\"operationType\":\"AUTHORIZATION\"}";

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PendingOperationService pendingOperationService;

    @InjectMocks
    private PendingOperationKafkaListener kafkaListener;

    private final OperationMessage operationMessage = new OperationMessage();

    @Test
    void testOnMessage_authorization() throws JsonProcessingException {
        operationMessage.setStatus(AUTHORIZATION);
        when(objectMapper.readValue(MESSAGE, OperationMessage.class)).thenReturn(operationMessage);

        kafkaListener.onMessage(MESSAGE);

        verify(pendingOperationService).authorization(operationMessage);
        verifyNoMoreInteractions(pendingOperationService);
    }

    @Test
    void testOnMessage_clearing() throws JsonProcessingException {
        operationMessage.setStatus(CLEARING);
        when(objectMapper.readValue(MESSAGE, OperationMessage.class)).thenReturn(operationMessage);

        kafkaListener.onMessage(MESSAGE);

        verify(pendingOperationService).clearing(operationMessage);
        verifyNoMoreInteractions(pendingOperationService);
    }

    @Test
    void testOnMessage_cancellation() throws JsonProcessingException {
        operationMessage.setStatus(CANCELLATION);
        when(objectMapper.readValue(MESSAGE, OperationMessage.class)).thenReturn(operationMessage);

        kafkaListener.onMessage(MESSAGE);

        verify(pendingOperationService).cancellation(operationMessage);
        verifyNoMoreInteractions(pendingOperationService);
    }

    @Test
    void testOnMessage_error() throws JsonProcessingException {
        operationMessage.setStatus(ERROR);
        when(objectMapper.readValue(MESSAGE, OperationMessage.class)).thenReturn(operationMessage);

        kafkaListener.onMessage(MESSAGE);

        verify(pendingOperationService).cancellation(operationMessage);
        verifyNoMoreInteractions(pendingOperationService);
    }

    @Test
    void testOnMessage_unknownTypeException() throws JsonProcessingException {
        operationMessage.setStatus(UNKNOWN);
        when(objectMapper.readValue(MESSAGE, OperationMessage.class)).thenReturn(operationMessage);

        assertThatThrownBy(() -> kafkaListener.onMessage(MESSAGE))
                .isInstanceOf(UnknownOperationException.class);
    }

    @Test
    void testOnMessage_jsonProcessingException() throws JsonProcessingException {
        when(objectMapper.readValue(MESSAGE, OperationMessage.class)).thenThrow(new JsonProcessingException("Error") {});

        assertThatThrownBy(() -> kafkaListener.onMessage(MESSAGE))
                .isInstanceOf(ApiException.class);
    }
}