package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {
    private final FreeAccountNumbersService freeAccountNumbersService;

    @Value("${scheduler.account-number.batch-size}")
    private int batchSize;

    @Scheduled(cron = "${scheduler.account-number.cron}")
    public void generateAccountNumbers () {
        for (AccountType type : AccountType.values()) {
            log.debug("Scheduled generating account numbers for type {} - Started", type);
            freeAccountNumbersService.generateAccountNumbers(type, batchSize);
            log.debug("Scheduled generating account numbers for type {} - Finished", type);
        }
    }
}