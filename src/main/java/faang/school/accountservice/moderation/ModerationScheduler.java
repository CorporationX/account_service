package faang.school.accountservice.moderation;

import faang.school.accountservice.service.account.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModerationScheduler {

    private final SavingsAccountService savingsAccountService;

    @Scheduled(cron = "${scheduler.calculatePercents.cron}")
    public void calculatePercentsForSavingsAccounts() {
        savingsAccountService.calculatePercents();
    }
}
