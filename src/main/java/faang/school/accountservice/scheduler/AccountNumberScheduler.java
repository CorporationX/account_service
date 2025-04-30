package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.free_account.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountNumberScheduler {

    private final FreeAccountNumberService freeAccountNumberService;

    @Scheduled(cron = "${scheduler.cron-credit}")
    public void generateAccountNumberCredit() {
        try {
            freeAccountNumberService.generateAccountNumber(AccountType.CREDIT);
        } catch (Exception e) {
            log.error("Failed to generate account numbers for CREDIT type", e);
        }
    }

    @Scheduled(cron = "${scheduler.cron-deposit}")
    public void generateAccountNumberDeposit() {
        try {
            freeAccountNumberService.generateAccountNumber(AccountType.DEPOSIT);
        } catch (Exception e) {
            log.error("Failed to generate account numbers for DEPOSIT type", e);
        }
    }
}
