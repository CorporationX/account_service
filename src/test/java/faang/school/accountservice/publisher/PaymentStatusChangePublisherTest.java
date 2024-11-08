package faang.school.accountservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.PendingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentStatusChangePublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentStatusChangePublisher publisher;

    private String topicName;
    private PendingDto testEvent;

    @BeforeEach
    void setUp() {
        topicName = "test-topic";
        testEvent = new PendingDto();
        ReflectionTestUtils.setField(publisher, "topicName", topicName);
    }

    @Test
    void publish_shouldSendEventToKafka() throws JsonProcessingException {
        String jsonEvent = "{\"status\":\"SUCCESS\"}";
        when(objectMapper.writeValueAsString(testEvent)).thenReturn(jsonEvent);
        when(kafkaTemplate.executeInTransaction(any())).thenAnswer(invocation -> {
            KafkaOperations.OperationsCallback<String, String, Boolean> callback = invocation.getArgument(0);
            return callback.doInOperations(kafkaTemplate);
        });

        publisher.publish(testEvent);

        verify(kafkaTemplate).executeInTransaction(any());
        verify(kafkaTemplate).send(topicName, jsonEvent);
    }

    @Test
    void publish_shouldThrowExceptionWhenJsonProcessingFails() {
        when(kafkaTemplate.executeInTransaction(any())).thenAnswer(invocation -> {
            KafkaOperations.OperationsCallback<String, String, Boolean> callback = invocation.getArgument(0);
            when(objectMapper.writeValueAsString(testEvent)).thenThrow(new JsonProcessingException("Ошибка JSON") {});
            return callback.doInOperations(kafkaTemplate);
        });

        assertThrows(RuntimeException.class, () -> publisher.publish(testEvent));

        verify(kafkaTemplate).executeInTransaction(any());
        verify(kafkaTemplate, never()).send(topicName, anyString());
    }
}

