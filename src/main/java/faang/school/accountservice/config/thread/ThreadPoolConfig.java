package faang.school.accountservice.config.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {
    private static final int THREAD_POOL_SIZE = 10;

    @Bean
    public ExecutorService calculateAccrualsExecutorService() {
        return Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }
}
