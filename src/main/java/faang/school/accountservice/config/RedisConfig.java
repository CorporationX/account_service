package faang.school.accountservice.config;

import faang.school.accountservice.listener.PaymentEventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${redis.channels.payment}")
    private String paymentEventChannel;

    @Value("${redis.channels.payment-status}")
    private String paymentStatusEventChannel;

    public interface MessagePublisher<T> {
        void publish(T redisEvent);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public ChannelTopic paymentChannelTopic() {
        return new ChannelTopic(paymentEventChannel);
    }

    @Bean
    public ChannelTopic paymentStatusChannelTopic() {
        return new ChannelTopic(paymentStatusEventChannel);
    }

    @Bean
    public MessageListenerAdapter paymentEventListenerAdapter(PaymentEventListener paymentEventListener) {
        return new MessageListenerAdapter(paymentEventListener, "onMessage");
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(
            LettuceConnectionFactory lettuceConnectionFactory,
            PaymentEventListener paymentEventListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(lettuceConnectionFactory);
        container.addMessageListener(paymentEventListenerAdapter(paymentEventListener), paymentChannelTopic());
        return container;
    }
}