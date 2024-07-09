package faang.school.accountservice.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {

    private int port;
    private String host;
    private Channels channels;

    @Data
    static class Channels {
        private String payment;
    }
}
