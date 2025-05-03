package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.interfaces.FreeAccountNumberService;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class AccountNumberRetrievalChecker implements SchedulingConfigurer {

    @Value("${account.number.check.enabled}")
    private Boolean enabled;

    @Value("${account.number.check.fixed-rate}")
    private Long fixedRate;

    private final FreeAccountNumberService freeAccountNumberService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(AccountType.values().length);

    @Override
    public void configureTasks(@NonNull ScheduledTaskRegistrar taskRegistrar) {
        if (!enabled) {
            log.info("Account number retrieval check is disabled");
            return;
        }

        taskRegistrar.addFixedRateTask(this::checkFreeAccountNumbers, fixedRate);
        log.info("Scheduled account number retrieval check with fixed rate: {} ms", fixedRate);
    }

    public void checkFreeAccountNumbers() {
        log.info("Starting check of free account numbers retrieval for all types");

        Arrays.stream(AccountType.values())
                .forEach(type -> executorService.submit(() -> checkForType(type)));
    }

    private void checkForType(@NotNull AccountType type) {
        try {
            log.info("Attempting to retrieve and use a free account number for type: {}", type);

            freeAccountNumberService.useFreeAccountNumber(type, accountNumber ->
                    log.info("Successfully retrieved and used account number for type: {}," +
                            " accountNumber: {}", type, accountNumber));

        } catch (Exception e) {
            log.error("Failed to retrieve or use account number for type: {}. Error: {}",
                    type, e.getMessage(), e);
        }
    }
}
