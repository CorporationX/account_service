package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.service.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {
    private final FreeAccountNumberService freeAccountNumberService;
    private final AccountType[] accountTypes = AccountType.values();
    private final ExecutorService executorService = Executors.newFixedThreadPool(accountTypes.length);
    @Value("${account.number.batch_size}")
    private int batchSize;

    @Scheduled(cron = "${account.number.cron_to_create_new_numbers}")
    public void generateAccountNumbers() {
        Arrays.stream(accountTypes).forEach(accountType -> executorService.execute(() -> {
            int freeNumbers = freeAccountNumberService.getQuantityFreeAccountNumbersByType(accountType);
            int neededNumbers = batchSize - freeNumbers;
            if (neededNumbers > 0) {
                freeAccountNumberService
                        .generateAccountNumbers(accountType, neededNumbers);
            }
        }));
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
