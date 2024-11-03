package faang.school.accountservice.config.async;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorServiceConfig {

    private ExecutorService mainExecutorService;

    @Bean
    public ExecutorService mainExecutorService(@Value("${poolThreads.mainPoolSize}") int mainPoolSize) {
        mainExecutorService = Executors.newFixedThreadPool(mainPoolSize);
        return mainExecutorService;
    }

    @PreDestroy
    public void shutdown() {
        mainExecutorService.shutdown();

        try {
            if (!mainExecutorService.awaitTermination(1, TimeUnit.MINUTES)) {
                mainExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            mainExecutorService.shutdownNow();
        }
    }
}
