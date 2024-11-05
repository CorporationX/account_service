package faang.school.accountservice.moderation;

import faang.school.accountservice.service.account.SavingsAccountService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@EnableRetry
@Component
@RequiredArgsConstructor
public class ModerationScheduler {

    private final SavingsAccountService savingsAccountService;

    @Retryable(retryFor = {IOException.class, OptimisticLockException.class})
    @Scheduled(cron = "${scheduler.calculatePercents.cron}")
    public void calculatePercentsForSavingsAccounts() {
        savingsAccountService.calculatePercents();
    }
}
