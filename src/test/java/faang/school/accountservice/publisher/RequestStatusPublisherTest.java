package faang.school.accountservice.publisher;

import faang.school.accountservice.config.redis.RedisProperties;
import faang.school.accountservice.dto.RequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RequestStatusPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Spy
    private RedisProperties redisProperties;

    @InjectMocks
    private RequestStatusPublisher requestStatusPublisher;

    @Test
    void publish_shouldSendEventToRedisChannel() {
        RequestDto event = new RequestDto();
        String channel = "test_channel";
        Map<String, String> channels = Map.of("request_status_notification", channel);
        redisProperties.setChannels(channels);

        requestStatusPublisher.publish(event);

        verify(redisTemplate).convertAndSend(channel, event);
    }
}
