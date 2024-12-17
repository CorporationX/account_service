package faang.school.accountservice.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.redis")
public record Payments_DMS(Integer port, String host, String channel_recipient, String channel_sender) {
}