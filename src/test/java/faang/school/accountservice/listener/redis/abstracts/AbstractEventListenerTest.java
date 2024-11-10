package faang.school.accountservice.listener.redis.abstracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractEventListenerTest {
    private static final String TOPIC_NAME = "topic-name";
    private static final UUID UUID_TEST = UUID.randomUUID();

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BalanceService balanceService;

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

        testEventListener.onMessage(message, null);

        verify(balanceService).findById(UUID_TEST);
    }

    @Test
    void tesSaveEvent_successful() {
        TestEvent event = new TestEvent("This is a valid message");

        testEventListener.saveEvent(event);
        verify(balanceService).findById(UUID_TEST);
    }

    @Test
    void testGetTopic_successful() {
        assertThat(testEventListener.getTopic().getTopic()).isEqualTo(TOPIC_NAME);
    }

    @Test
    void testHandleException_successful() {
        assertThatThrownBy(() -> testEventListener.handleException(new RuntimeException()))
                .isInstanceOf(RuntimeException.class);
    }

    private static class TestEventListener extends AbstractEventListener<TestEvent> {
        private final BalanceService balanceService;

        public TestEventListener(ObjectMapper objectMapper, BalanceService balanceService) {
            super(objectMapper, TestEvent.class);
            this.balanceService = balanceService;
        }

        @Override
        public void saveEvent(TestEvent event) {
            balanceService.findById(UUID_TEST);
        }

        @Override
        public Topic getTopic() {
            return new ChannelTopic(TOPIC_NAME);
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