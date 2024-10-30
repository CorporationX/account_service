package faang.school.accountservice.publisher;

import faang.school.accountservice.config.redis.RedisProperties;
import faang.school.accountservice.dto.OpenAccountEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class OpenAccountEventPublisher implements EventPublisher<OpenAccountEvent> {
    private final RedisTemplate<String, Object> redisTemplateConfig;
    private final RedisProperties redisProperties;

    public void publish(OpenAccountEvent event) {
        String channelName = redisProperties.getChannels().get("project-event-channel");
        redisTemplateConfig.convertAndSend(channelName, event);
        log.info("Published event {}", event);
    }
}
