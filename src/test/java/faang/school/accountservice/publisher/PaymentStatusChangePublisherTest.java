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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
    private PendingDto pendingDto;

    @BeforeEach
    void setUp() {
        topicName = "topicName";
        pendingDto = new PendingDto();

        ReflectionTestUtils.setField(publisher, "topicName", topicName);
    }

    @Test
    void publish_shouldSendMessageToKafka_whenEventIsValid() throws JsonProcessingException {
        String jsonEvent = "{\"sample\":\"data\"}";
        when(objectMapper.writeValueAsString(pendingDto)).thenReturn(jsonEvent);

        publisher.publish(pendingDto);

        verify(objectMapper).writeValueAsString(pendingDto);
        verify(kafkaTemplate).send(topicName, jsonEvent);
    }

    @Test
    void publish_shouldThrowRuntimeException_whenJsonProcessingExceptionOccurs() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(pendingDto)).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> publisher.publish(pendingDto));

        verify(objectMapper).writeValueAsString(pendingDto);
        verifyNoInteractions(kafkaTemplate);
    }
}
