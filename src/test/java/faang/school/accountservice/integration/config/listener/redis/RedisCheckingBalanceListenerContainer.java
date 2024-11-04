package faang.school.accountservice.integration.config.listener.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Profile("testLiquibaseRedis")
@Configuration
public class RedisCheckingBalanceListenerContainer {
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(@Value("${redis.topic.checking_balance}")
                                                                       String topic,
                                                                       JedisConnectionFactory jedisConnectionFactory,
                                                                       RedisCheckingBalanceListener redisCheckingBalanceListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        container.addMessageListener(redisCheckingBalanceListener, new ChannelTopic(topic));
        return container;
    }
}
