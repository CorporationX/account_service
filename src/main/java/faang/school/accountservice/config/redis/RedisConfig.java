package faang.school.accountservice.config.redis;

import faang.school.accountservice.config.redis.adapter.EventListenerAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final List<EventListenerAdapter> adapters;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        adapters.forEach(
            adapter -> container.addMessageListener(adapter.getAdapter(), adapter.getChannelTopic())
        );

        return container;
    }
}
