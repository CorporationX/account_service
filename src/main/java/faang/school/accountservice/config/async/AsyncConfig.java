package faang.school.accountservice.config.async;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig  implements AsyncConfigurer {

    public static final String SCHEDULED_EXECUTOR = "scheduledExecutor";

    private final ExecutorsConfig executorsConfig;

    @Bean
    public Executor scheduledExecutor() {
        ExecutorsConfig.ExecutorProps scheduleExecutorProps = executorsConfig.getSchedule();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(scheduleExecutorProps.getCorePoolSize());
        executor.setMaxPoolSize(scheduleExecutorProps.getMaxPoolSize());
        executor.setQueueCapacity(scheduleExecutorProps.getQueueCapacity());
        executor.setThreadNamePrefix(scheduleExecutorProps.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}
