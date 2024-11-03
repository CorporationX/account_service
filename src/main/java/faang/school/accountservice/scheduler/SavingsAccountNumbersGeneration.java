package faang.school.accountservice.scheduler;

import faang.school.accountservice.model.enums.AccountType;
import faang.school.accountservice.service.AccountService;
import faang.school.accountservice.service.impl.FreeAccountNumbersServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SavingsAccountNumbersGeneration {
    private final FreeAccountNumbersServiceImpl freeAccountNumbersServiceImpl;

    @Scheduled(cron = "0 0 1 * * *")
    @Retryable(backoff = @Backoff(delay = 5000))
    protected void savingsAccountNumberGenerator() {
        freeAccountNumbersServiceImpl.ensureMinimumAccountNumbers(AccountType.SAVINGS, 100);
    }
}
