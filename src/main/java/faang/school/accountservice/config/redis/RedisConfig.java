package faang.school.accountservice.config.redis;

import faang.school.accountservice.redis.PaymentEventListener;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@RequiredArgsConstructor
public class RedisConfig {
    private int port;
    private String host;
    private String channel;
    private final PaymentEventListener paymentEventLister;

    @Bean
    public ChannelTopic paymentTopic() {
        return new ChannelTopic(channel);
    }

    @Bean
    public RedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public MessageListenerAdapter paymentEventListenerAdapter() {
        return new MessageListenerAdapter(paymentEventLister);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer() {
        var container = new RedisMessageListenerContainer();

        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(paymentEventListenerAdapter(), paymentTopic());

        return container;
    }
}
