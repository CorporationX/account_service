package faang.school.accountservice.config.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class ExecutorConfig {
    private final ExecutorProperties executorProperties;

    @Bean
    public ThreadPoolExecutor amountHandlerExecutor() {
        return createThreadPoolExecutor();
    }

    @Bean
    public ThreadPoolExecutor accountRecordHandlerExecutor() {
        return createThreadPoolExecutor();
    }

    @Bean
    public ThreadPoolExecutor auditRecordHandlerExecutor() {
        return createThreadPoolExecutor();
    }

    @Bean
    public ThreadPoolExecutor balanceRecordHandlerExecutor() {
        return createThreadPoolExecutor();
    }

    @Bean
    public ThreadPoolExecutor openAccountNotificationHandlerExecutor() {
        return createThreadPoolExecutor();
    }

    @Bean
    public ThreadPoolExecutor cashbackRecordHandlerExecutor() {
        return createThreadPoolExecutor();
    }

    private ThreadPoolExecutor createThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                executorProperties.getCorePoolSize(),
                executorProperties.getMaxPoolSize(),
                executorProperties.getKeepAliveTime(),
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(executorProperties.getQueueCapacity())
        );
    }
}

