package faang.school.accountservice.publisher.payment.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractToKafkaPaymentPublisherTest {
    private static final String JSON = "{\"message\":\"This is a valid message\"}";
    private static final String TOPIC = "topic";

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TestPublisher testPublisher;

    @Test
    void testPublish_jsonParseException() throws JsonProcessingException {
        TestEvent event = new TestEvent("This is a valid message");
        testPublisher.makeResponse(event);

        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("") {
        });

        assertThatThrownBy(testPublisher::publish).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testPublish_successful() throws JsonProcessingException {
        TestEvent event = new TestEvent("This is a valid message");
        testPublisher.makeResponse(event);

        when(objectMapper.writeValueAsString(event)).thenReturn(JSON);

        testPublisher.publish();

        verify(kafkaTemplate).send(TOPIC, JSON);
    }

    @Test
    void testGetInstance_successful() {
        assertThat(testPublisher.getInstance()).isEqualTo(TestEvent.class);
    }

    private static class TestPublisher extends AbstractToKafkaPaymentPublisher<TestEvent> {
        private static final String TOPIC = "topic";

        public TestPublisher(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
            super(kafkaTemplate, objectMapper, TestEvent.class);
        }

        @Override
        public void makeResponse(Object... args) {
            TestEvent testEvent = (TestEvent) args[0];
            setResponse(testEvent);
        }

        @Override
        public void makeErrorResponse(Object... args) {}

        @Override
        public String getTopicName() {
            return TOPIC;
        }
    }

    @Getter
    @Setter
    private static class TestEvent {
        private String message;

        public TestEvent(String message) {
            this.message = message;
        }
    }
}
