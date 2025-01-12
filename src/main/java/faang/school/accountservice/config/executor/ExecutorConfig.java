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
    public ThreadPoolExecutor checkAccountLimitHandlerExecutor(){
        return createThreadPoolExecutor();
    }

    @Bean
    public ThreadPoolExecutor createRecordAccountHandlerExecutor(){
        return createThreadPoolExecutor();
    }

    @Bean
    public ThreadPoolExecutor createRecordBalanceHandlerExecutor(){
        return createThreadPoolExecutor();
    }

    @Bean
    public ThreadPoolExecutor createRecordCashBackHandlerExecutor(){
        return createThreadPoolExecutor();
    }

    @Bean
    public ThreadPoolExecutor sendNotificationHandlerExecutor(){
        return createThreadPoolExecutor();
    }

    private ThreadPoolExecutor createThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                executorProperties.corePoolSize(),
                executorProperties.maxPoolSize(),
                executorProperties.keepAliveTime(),
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(executorProperties.queueCapacity())
        );
    }
}
