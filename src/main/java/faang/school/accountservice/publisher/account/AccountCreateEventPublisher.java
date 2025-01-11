package faang.school.accountservice.publisher.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.publisher.MessagePublish;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountCreateEventPublisher implements MessagePublish<AccountCreateEvent> {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.account_create_channel.name}")
    private String channelName;

    @Override
    public void publish(AccountCreateEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channelName, message);
        } catch (JsonProcessingException e) {
            log.error("JSON serialization error. Event: {}. Error: {}", event, e.getMessage(), e);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failure while publishing event to channel '{}'. Event: {}. Error: {}",
                    channelName, event, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while publishing event to channel '{}'. Event: {}. Error: {}",
                    channelName, event, e.getMessage(), e);
        }
    }
}
