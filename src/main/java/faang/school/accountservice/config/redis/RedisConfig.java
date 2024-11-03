package faang.school.accountservice.config.redis;

import faang.school.accountservice.listener.AchievementEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RedisConfig {

    private static final String DEFAULT_LISTENER_METHOD = "onMessage";

    @Value("${redis.channels.achievement}")
    private String achievementEventChannel;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public ChannelTopic achievementChannelTopic() {
        return new ChannelTopic(achievementEventChannel);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(
            LettuceConnectionFactory lettuceConnectionFactory,
            AchievementEventListener achievementEventListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(lettuceConnectionFactory);
        container.addMessageListener(achievementEventListenerAdapter(achievementEventListener), achievementChannelTopic());
        return container;
    }

    @Bean
    public MessageListenerAdapter achievementEventListenerAdapter(AchievementEventListener achievementEventListener) {
        return new MessageListenerAdapter(achievementEventListener, DEFAULT_LISTENER_METHOD);
    }
}
