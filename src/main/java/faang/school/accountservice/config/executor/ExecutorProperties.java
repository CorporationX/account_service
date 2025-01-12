package faang.school.accountservice.config.executor;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "executor")
public record ExecutorProperties(
        int corePoolSize,
        int maxPoolSize,
        int queueCapacity,
        int keepAliveTime
) {
}
