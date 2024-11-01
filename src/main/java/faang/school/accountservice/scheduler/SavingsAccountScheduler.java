package faang.school.accountservice.scheduler;

import faang.school.accountservice.model.enums.AccountType;
import faang.school.accountservice.service.impl.FreeAccountNumbersServiceImpl;
import faang.school.accountservice.service.impl.SavingsAccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.dvcs.DVCSObjectIdentifiers;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SavingsAccountScheduler {
    private final FreeAccountNumbersServiceImpl freeAccountNumbersServiceImpl;
    private final SavingsAccountServiceImpl savingsAccountService;

    @Scheduled(cron = "0 0 1 * * *")
    @Retryable(backoff = @Backoff(delay = 5000))
    protected void savingsAccountNumberGenerator() {
        freeAccountNumbersServiceImpl.ensureMinimumAccountNumbers(AccountType.SAVINGS, 100);
    }

    @Scheduled(cron = "0 * * * * *")
    @Retryable(backoff = @Backoff(delay = 5000))
    protected void calculatePercents() {
        // TODO надо что то сделать

    }

    @Async("calculatePercentsExecutor")
    public void calcPercent() {

    }

}
