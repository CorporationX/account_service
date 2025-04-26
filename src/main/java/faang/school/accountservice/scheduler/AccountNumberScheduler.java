package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.free_account.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {

    private final FreeAccountNumberService freeAccountNumberService;

    @Scheduled(cron = "${scheduler.cron-credit}")
    public void generateAccountNumberCredit() {
        freeAccountNumberService.generateAccountNumber(AccountType.CREDIT);
    }

    @Scheduled(cron = "${scheduler.cron-deposit}")
    public void generateAccountNumberDeposit() {
        freeAccountNumberService.generateAccountNumber(AccountType.DEPOSIT);
    }
}
