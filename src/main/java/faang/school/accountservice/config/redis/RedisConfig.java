package faang.school.accountservice.config.redis;

import faang.school.accountservice.listener.DmsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final Payments_DMS paymentsDms;

    @Bean
    LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration =
                new RedisStandaloneConfiguration(paymentsDms.host(), paymentsDms.port());
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    ChannelTopic channelRecipient() {
        return new ChannelTopic(paymentsDms.channel_recipient());
    }

    @Bean
    ChannelTopic channelSender() {
        return new ChannelTopic(paymentsDms.channel_sender());
    }

    @Bean
    RedisMessageListenerContainer listenerContainer(LettuceConnectionFactory lettuceConnectionFactory,
                                                    DmsListener dmsListener,
                                                    ChannelTopic channelRecipient) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(lettuceConnectionFactory);
        container.addMessageListener(dmsListener, channelRecipient);
        return container;
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setConnectionFactory(lettuceConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jsonSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public AnswerSendingTest testAnswer(RedisTemplate<String, Object> redisTemplate, ChannelTopic channelSender) {
        return new AnswerSendingTest(redisTemplate, channelSender);
    }
}