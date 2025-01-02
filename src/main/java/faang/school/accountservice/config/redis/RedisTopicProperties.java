package faang.school.accountservice.config.redis;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class RedisTopicProperties {

    @Value("${spring.data.redis.channels.request-in-progress-notification-channel.name}")
    private String requestInProgressTopic;
}
