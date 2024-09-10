package faang.school.accountservice.scheduler.account;

import faang.school.accountservice.config.account.AccountGenerateConfig;
import faang.school.accountservice.service.FreeAccountNumbersGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountGenerationScheduler {
    private final AccountGenerateConfig accountGenerateConfig;
    private final FreeAccountNumbersGenerationService generationService;

    @Scheduled(cron = "${task.accounts-generation.schedule.frequency}")
    public void generateFreeAccounts() {
        log.info("Starting generates Accounts");
        for (var accountsEntrySet : accountGenerateConfig.getAccountsQuantity().entrySet()) {
            generationService.generateNewAccounts(accountsEntrySet.getKey(), accountsEntrySet.getValue());
            log.info("Generated numbers: {} of accounts type: {}", accountsEntrySet.getValue(), accountsEntrySet.getKey());
        }
    }
}
