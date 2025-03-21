package faang.school.accountservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableScheduling
@Configuration
public class ExecutorServiceConfiguration {

    @Value("${rate-change.executor.core-pool-size}")
    private int corePoolSize;

    @Value("${rate-change.executor.max-pool-size}")
    private int maxPoolSize;

    @Value("${rate-change.executor.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "rateChange")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        return executor;
    }
}
