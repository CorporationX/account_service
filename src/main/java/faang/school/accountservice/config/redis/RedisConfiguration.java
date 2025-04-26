package faang.school.accountservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.events.RequestEventEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class RedisConfiguration {

    private final RedisConfig redisConfig;
    private final ObjectMapper objectMapper;

    @Bean(name = "requestEventsRedisTemplate")
    @SuppressWarnings("unused")
    public RedisTemplate<String, RequestEventEvent> requestEventsRedisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RequestEventEvent> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        var jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();

        return template;
    }

    @Bean(name = "requestEventsTopic")
    @SuppressWarnings("unused")
    ChannelTopic requestEventsTopic() {
        return new ChannelTopic(redisConfig.getChannels().get("request_events_channel").name());
    }
}