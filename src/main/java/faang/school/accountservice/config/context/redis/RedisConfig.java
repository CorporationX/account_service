package faang.school.accountservice.config.context.redis;

import faang.school.accountservice.listener.AbstractEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisConnectionFactory connectionFactory;

    @Bean
    public RedisMessageListenerContainer redisContainer(List<AbstractEventListener> eventListeners) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);
        for (AbstractEventListener listener : eventListeners) {
            container.addMessageListener(listener.createListenerAdapter(), new ChannelTopic(listener.getTopic()));
        }
        return container;
    }

}
