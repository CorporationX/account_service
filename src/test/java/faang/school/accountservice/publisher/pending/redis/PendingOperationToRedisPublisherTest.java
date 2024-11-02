package faang.school.accountservice.publisher.pending.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.balance.response.CheckingAccountBalance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;

import java.util.UUID;

import static faang.school.accountservice.enums.pending.AccountBalanceStatus.SUFFICIENT_FUNDS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PendingOperationToRedisPublisherTest {
    private static final UUID OPERATION_ID = UUID.randomUUID();
    private static final UUID SOURCE_ACCOUNT_ID = UUID.randomUUID();
    private static final String TOPIC = "topic";
    private static final String JSON = "json";

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private Topic topic;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PendingOperationToRedisPublisher publisher;

    @Test
    void testPublish_jsonException() throws JsonProcessingException {
        CheckingAccountBalance checkingAccountBalance =
                new CheckingAccountBalance(OPERATION_ID, SOURCE_ACCOUNT_ID, SUFFICIENT_FUNDS);

        when(objectMapper.writeValueAsString(checkingAccountBalance)).thenThrow(new JsonProcessingException("Error") {
        });

        assertThatThrownBy(() -> publisher.publish(checkingAccountBalance)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testPublish_successful() throws JsonProcessingException {
        CheckingAccountBalance checkingAccountBalance =
                new CheckingAccountBalance(OPERATION_ID, SOURCE_ACCOUNT_ID, SUFFICIENT_FUNDS);

        when(objectMapper.writeValueAsString(checkingAccountBalance)).thenReturn(JSON);
        when(topic.getTopic()).thenReturn(TOPIC);

        publisher.publish(checkingAccountBalance);

        verify(redisTemplate).convertAndSend(TOPIC, JSON);
    }
}
