package faang.school.accountservice.config.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {

    @Value("${account.interest.scheduler.thread-pool.max-size}")
    private int maxSize;

    @Bean(name = "interestCalculationThreadPool")
    public ExecutorService customThreadPool() {
        return Executors.newFixedThreadPool(maxSize);
    }
}

