package faang.school.accountservice.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.data.redis")
public class RedisProperties {
    private int port;
    private String host;
}
