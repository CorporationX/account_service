package faang.school.accountservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.dms.DmsEventDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration("redisConfigForTest")
public class RedisConfig {

    @Bean
    public RedisTemplate<String, DmsEventDto> redisTemplateDmsEvent(
        JedisConnectionFactory jedisConnectionFactory, ObjectMapper objectMapper
    ) {
        RedisTemplate<String, DmsEventDto> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, DmsEventDto.class));

        return redisTemplate;
    }
}
