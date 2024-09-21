package faang.school.accountservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Value("${executor.cashback-tariff.core-pool-size:25}")
    private int cashbackTariffCorePoolSize;

    @Value("${executor.cashback-tariff.max-pool-size:50}")
    private int cashbackTariffMaxPoolSize;

    @Value("${executor.cashback-tariff.queue-capacity:200}")
    private int cashbackTariffQueueCapacity;

    @Value("${executor.cashback-tariff.prefix:CashbackTariff-}")
    private String cashbackTariffPrefix;

    @Bean
    public Executor cashbackTariffExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cashbackTariffCorePoolSize);
        executor.setMaxPoolSize(cashbackTariffMaxPoolSize);
        executor.setQueueCapacity(cashbackTariffQueueCapacity);
        executor.setThreadNamePrefix(cashbackTariffPrefix);
        executor.initialize();
        return executor;
    }
}
