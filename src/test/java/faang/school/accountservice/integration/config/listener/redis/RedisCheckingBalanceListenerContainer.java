package faang.school.accountservice.integration.config.listener.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.List;

@Profile("testLiquibaseRedis")
@Configuration
public class RedisCheckingBalanceListenerContainer {
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(JedisConnectionFactory jedisConnectionFactory,
                                                                       List<TestEventMessageListener> listeners) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        listeners.forEach(listener -> container.addMessageListener(listener, listener.getTopic()));
        
        return container;
    }
}
