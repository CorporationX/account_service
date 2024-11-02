package faang.school.accountservice.config.redis.listener;

import faang.school.accountservice.listener.redis.abstracts.EventMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.List;

@Configuration
public class RedisListenerContainerConfig {
    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory,
                                                        List<EventMessageListener> listeners) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        listeners.forEach(listener -> container.addMessageListener(listener, listener.getTopic()));

        return container;
    }
}
