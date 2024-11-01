package faang.school.accountservice.config.executor;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "executor")
@RequiredArgsConstructor
@Data
public class ExecutorProperties {
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private int keepAliveTime;
}
