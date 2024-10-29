package faang.school.accountservice.config.redis;

import faang.school.accountservice.publis.listener.payment.PaymentEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties redisProperties;
    private final PaymentEventListener paymentEventListener;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    @Bean
    public RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer redisContainer = new RedisMessageListenerContainer();
        redisContainer.setConnectionFactory(redisConnectionFactory());

        redisContainer.addMessageListener(paymentListenerAdapter(), paymentTopic());

        return redisContainer;
    }

    @Bean
    public MessageListener paymentListenerAdapter() {
        return new MessageListenerAdapter(paymentEventListener);
    }

    @Bean
    public Topic paymentTopic() {
        return new ChannelTopic(redisProperties.getPaymentEventChannelName());
    }
}
