package faang.school.accountservice.scheduler;

import faang.school.accountservice.entity.*;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.AccountCashbackProcessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
public class CashbackScheduler {
    private final AccountRepository accountRepository;
    private final AccountCashbackProcessorService accountCashbackProcessorService;
    private final Executor cashbackTariffExecutor;

    // TODO Fix this moment with List<BalanceAudits>
    @Scheduled(cron = "${cashback-tariff.cron}:0 0 0 1 * ?")
    public void calculateMonthlyCashback() {
        List<Account> accounts = accountRepository.findAll();

        List<CompletableFuture<Void>> completableFutures = accounts.stream()
            .map(account -> CompletableFuture.runAsync(
                () -> accountCashbackProcessorService.processAccountCashback(account),
                cashbackTariffExecutor
            ))
            .toList();
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));

    }
}
