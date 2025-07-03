package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {
    private final FreeAccountNumbersService freeAccountNumbersService;
    private final AccountNumberTaskProperties properties;

    @Scheduled(cron = "${scheduler.account-number.cron}")
    @SchedulerLock(
            name = "${scheduler.account-number.lock-name}",
            lockAtLeastFor = "${scheduler.account-number.lock-at-least-for}",
            lockAtMostFor = "${scheduler.account-number.lock-at-most-for}"
    )
    public void generateAccountNumbers () {
        for (AccountType type : AccountType.values()) {
            log.debug("Scheduled generating account numbers for type {} - Started", type);
            freeAccountNumbersService.generateAccountNumbers(type, properties.getBatchSize());
            log.debug("Scheduled generating account numbers for type {} - Finished", type);
        }
    }
}