package faang.school.accountservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfig {
    @Value("${thread_pools.size}")
    private int threadPoolSize;

    @Bean
    public ExecutorService getThreadPool() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}