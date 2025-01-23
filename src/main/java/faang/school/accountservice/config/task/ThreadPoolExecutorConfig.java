package faang.school.accountservice.config.task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolExecutorConfig {
    @Value("${task.saving-accounts.pool.max-size}")
    private int poolMaxSize;
    @Value("${task.saving-accounts.pool.core-size}")
    private int poolCoreSize;
    @Value("${task.saving-accounts.pool.queue-capacity}")
    private int queueCapacity;

    @Bean
    public TaskExecutor savingAccountPayoffInterestTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(poolMaxSize);
        executor.setCorePoolSize(poolCoreSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }
}
