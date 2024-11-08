package faang.school.accountservice.config.executor;

import faang.school.accountservice.config.kafka.KafkaProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorServiceConfig {

    @Value("${executor.threads.count}")
    private int treadCount;

    private final KafkaProperties kafkaProperties;

    public ExecutorServiceConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public ExecutorService executor() {
        return Executors.newFixedThreadPool(treadCount);
    }

    @Bean
    public ExecutorService executorServiceAsync() {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(kafkaProperties.getCapacity());
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                kafkaProperties.getCorePoolSize(), kafkaProperties.getMaxPoolSize(),
                kafkaProperties.getKeepAliveTime(), TimeUnit.SECONDS,
                queue,
                new ThreadPoolExecutor.AbortPolicy()
        );
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }
}
