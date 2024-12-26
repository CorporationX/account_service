package faang.school.accountservice.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.redis")
public record RedisProperties(String host, int port, Channel channel) {
    public record Channel(String account) {
    }
}