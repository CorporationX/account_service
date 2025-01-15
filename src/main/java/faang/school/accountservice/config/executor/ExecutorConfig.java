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

    @Bean(name = "checkAccountLimitHandlerExecutor")
    public ThreadPoolExecutor checkAccountLimitHandlerExecutor(){
        return createThreadPoolExecutor();
    }

    @Bean(name = "recordAccountHandlerExecutor")
    public ThreadPoolExecutor createRecordAccountHandlerExecutor(){
        return createThreadPoolExecutor();
    }

    @Bean(name= "recordBalanceHandlerExecutor")
    public ThreadPoolExecutor createRecordBalanceHandlerExecutor(){
        return createThreadPoolExecutor();
    }

    @Bean(name = "recordCashBackHandlerExecutor")
    public ThreadPoolExecutor createRecordCashBackHandlerExecutor(){
        return createThreadPoolExecutor();
    }

    @Bean(name = "sendNotificationHandlerExecutor")
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
