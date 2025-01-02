package faang.school.accountservice.config.async;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "async.executors")
@Data
public class ExecutorsConfig {

    private ExecutorProps schedule;

    @Data
    public static class ExecutorProps {
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
        private String threadNamePrefix;
    }
}
