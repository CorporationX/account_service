package faang.school.accountservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TariffRatesCalculatingConfig {

    @Value("${thread-pool-setting.tariff-rate-calculator.size}")
    private int poolSize;

    @Value("${thread-pool-setting.tariff-rate-calculator.timeout}")
    private int shutdownTimeoutSeconds;

    @Bean(name = "tariffRatesCalculator")
    public ThreadPoolTaskExecutor createTariffRateCalculatorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setThreadNamePrefix("TariffRatesCalculatorPool-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(shutdownTimeoutSeconds);
        executor.initialize();
        return executor;
    }

}
