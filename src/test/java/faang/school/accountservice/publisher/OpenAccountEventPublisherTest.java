package faang.school.accountservice.publisher;

import faang.school.accountservice.config.redis.RedisProperties;
import faang.school.accountservice.dto.OpenAccountEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OpenAccountEventPublisherTest {
    @InjectMocks
    private OpenAccountEventPublisher publisher;
    @Mock
    private RedisTemplate<String, Object> redisTemplateConfig;
    @Mock
    private RedisProperties redisProperties;
    private OpenAccountEvent event;
    private String channelName;
    private Map<String, String> channelsMap;

    @BeforeEach
    void setUp() {
        event = new OpenAccountEvent();
        channelName = "project-event";
        channelsMap = new HashMap<>();
        channelsMap.put("project-event-channel", channelName);
    }

    @Test
    public void publishTest() {
        when(redisProperties.getChannels()).thenReturn(channelsMap);

        publisher.publish(event);

        verify(redisTemplateConfig, times(1)).convertAndSend(channelName, event);
    }
}
