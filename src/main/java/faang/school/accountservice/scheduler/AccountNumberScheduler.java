package faang.school.accountservice.scheduler;

import faang.school.accountservice.config.context.account_number.AccountNumberConfig;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.interfaces.FreeAccountNumberService;
import jakarta.annotation.PreDestroy;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class AccountNumberScheduler {

    private static final int TIME_AWAIT_TERMINATION_SEC = 60;
    private final AccountNumberConfig config;
    private final FreeAccountNumberService freeAccountNumberService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(AccountType.values().length);

    @Scheduled(cron = "${account.number.cron}")
    public void generateAccountNumbers() {
        log.info("Starting generation of account numbers for all types");

        Arrays.stream(AccountType.values())
                .forEach(type -> executorService.submit(() -> generateForType(type)));
    }

    private void generateForType(@NotNull AccountType type) {
        int batchSize = config.getBatchSize().getOrDefault(type.name(), config.getBatchSizeDefault());

        try {
            log.info("Starting generation of account numbers for type: {}, batchSize: {}", type, batchSize);
            freeAccountNumberService.generateAccountNumbers(type, batchSize);
            log.info("Successfully generated account numbers for type: {}", type);
        } catch (Exception e) {
            log.error("Failed to generate account numbers for type: {}. Error: {}", type, e.getMessage(), e);
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down AccountNumberScheduler executor service");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(TIME_AWAIT_TERMINATION_SEC, java.util.concurrent.TimeUnit.SECONDS)) {
                log.warn("Executor service did not terminate within 60 seconds, forcing shutdown");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for executor service to terminate", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
