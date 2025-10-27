package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {

    @Value("${account.number.batch.size}")
    private int batchSize;

    private final FreeAccountNumberService freeAccountNumberService;

    @Scheduled(cron = "0 0 0 * * *")
    public void generateDebit() {
        try {
            freeAccountNumberService.generateAccountNumbers(AccountType.DEBIT, batchSize);
            log.info("Successfully generated {} DEBIT account numbers", batchSize);
        } catch (Exception e) {
            log.error("Failed to generate DEBIT numbers", e);
        }
    }

    @Scheduled(cron = "0 5 0 * * *")
    public void generateCredit() {
        try {
            freeAccountNumberService.generateAccountNumbers(AccountType.CREDIT, batchSize);
            log.info("Successfully generated {} CREDIT account numbers", batchSize);
        } catch (Exception e) {
            log.error("Failed to generate CREDIT numbers", e);
        }
    }
}
