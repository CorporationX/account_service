package faang.school.accountservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CommonConfig {
    @Value("${thread-pool.scheduled.size:2}")
    private int poolSize;

    @Bean(destroyMethod = "shutdown")
    public ExecutorService scheduledThreadPool() {
        return Executors.newScheduledThreadPool(poolSize);
    }
}
