package faang.school.accountservice.listener.redis.abstracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.Topic;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractEventListenerTest {
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Topic topic;

    @Mock
    private Message message;

    @InjectMocks
    private TestEventListener testEventListener;

    @Test
    void testOnMessage_exception() throws IOException {
        String validJson = "{\"message\":\"This is a valid message\"}";
        byte[] bytes = validJson.getBytes();

        when(message.getBody()).thenReturn(bytes);
        when(objectMapper.readValue(bytes, TestEvent.class)).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> testEventListener.onMessage(message, null))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testOnMessage_successful() throws IOException {
        String validJson = "{\"message\":\"This is a valid message\"}";
        byte[] bytes = validJson.getBytes();
        TestEvent event = new TestEvent("This is a valid message");

        when(message.getBody()).thenReturn(bytes);
        when(objectMapper.readValue(bytes, TestEvent.class)).thenReturn(event);
        when(topic.getTopic()).thenReturn("topic");

        testEventListener.onMessage(message, null);

        verify(topic).getTopic();
    }

    @Test
    void tesSaveEvent_successful() {
        TestEvent event = new TestEvent("This is a valid message");
        when(topic.getTopic()).thenReturn("topic");
        testEventListener.saveEvent(event);
        verify(topic).getTopic();
    }

    @Test
    void testEventType_successful() {
        assertThat(testEventListener.getEventType()).isEqualTo(TestEvent.class);
    }

    @Test
    void testHandleException_successful() {
        assertThatThrownBy(() -> testEventListener.handleException(new RuntimeException()))
                .isInstanceOf(RuntimeException.class);
    }

    private static class TestEventListener extends AbstractEventListener<TestEvent> {
        private final Topic topic;
        public TestEventListener(ObjectMapper objectMapper, Topic topic) {
            super(objectMapper, topic);
            this.topic = topic;
        }

        @Override
        public void saveEvent(TestEvent event) {
            topic.getTopic();
        }

        @Override
        public Class<TestEvent> getEventType() {
            return TestEvent.class;
        }

        @Override
        public void handleException(Exception exception) {
            throw new RuntimeException();
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