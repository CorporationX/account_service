package faang.school.accountservice.scheduler;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.cashback.CashbackTariffService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
@Slf4j
@RequiredArgsConstructor
public class CashbackScheduler {
    private final CashbackTariffService cashbackTariffService;
    private final AccountRepository accountRepository;

    @Value("${cashback.batch-size}")
    private int batchSize;

    @Value("${cashback.thread-pool}")
    private int threadPool;

    private Executor executor;

    @PostConstruct
    public void init() {
        executor = Executors.newFixedThreadPool(threadPool);
    }

    @Scheduled(cron = "${cashback.scheduler.cron}")
    public void calculateCashback() {
        int offset = 0;
        List<Account> accounts;
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        LocalDateTime startOfLastMonth = lastMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfLastMonth = lastMonth.atEndOfMonth().atTime(23, 59, 59);

        do {
            Pageable pageable = PageRequest.of(offset, batchSize, Sort.by("id").ascending());
            accounts = accountRepository.findActiveAccountsWithCashbackTariff(pageable);

            List<CompletableFuture<Void>> futures = accounts.stream()
                    .map(account -> CompletableFuture.runAsync(() -> {
                        try {
                            cashbackTariffService.calculateCashback(account, startOfLastMonth, endOfLastMonth);
                        } catch (Exception exception) {
                            log.error("При подсчёте кешбека accountId={} произошла ошибка", account.getId(), exception);
                        }
                    }, executor))
                    .toList();

            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

            offset++;
        } while (!accounts.isEmpty());

        log.info("Завершён подсчёте кешбека за период с {} по {}", startOfLastMonth, endOfLastMonth);
    }
}
