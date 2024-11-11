package faang.school.accountservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.accountservice.config.redis.adapter.EventListenerAdapterConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final List<EventListenerAdapterConfig> adapters;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(
        @Value("${spring.data.redis.host}") String host,
        @Value("${spring.data.redis.port}") Integer port
    ) {
        return new JedisConnectionFactory(
            new RedisStandaloneConfiguration(host, port)
        );
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
        JedisConnectionFactory jedisConnectionFactory
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        adapters.forEach(
            adapter -> container.addMessageListener(adapter.getAdapter(), adapter.getChannelTopic())
        );

        return container;
    }
}
