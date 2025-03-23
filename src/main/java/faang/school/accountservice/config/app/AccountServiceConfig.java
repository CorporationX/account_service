package faang.school.accountservice.config.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AccountServiceConfig {

    private final int poolSize;

    public AccountServiceConfig(@Value("${account-service.thread-pool-size}") int poolSize) {
        this.poolSize = poolSize;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    ExecutorService threadPool() {
        return Executors.newFixedThreadPool(poolSize);
    }
}
