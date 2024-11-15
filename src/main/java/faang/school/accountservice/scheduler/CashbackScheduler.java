package faang.school.accountservice.scheduler;

import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.cashback.CashbackTariffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
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

    private ExecutorService executorService;

    //    @Scheduled(cron = "${cashback.scheduler.cron}")
    @Scheduled(fixedDelay = 1000000L)
    public void calculateCashback() {
        List<UUID> accounts = accountRepository.findActiveAccountsWithCashbackTariffIds();
        executorService = Executors.newFixedThreadPool(threadPool);
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        LocalDateTime startOfLastMonth = lastMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfLastMonth = lastMonth.atEndOfMonth().atTime(23, 59, 59);

        List<List<UUID>> batches = ListUtils.partition(accounts, batchSize);

        try {
            batches.forEach(batch -> {
                executorService.execute(() -> {
                    batch.forEach(accountId -> {
                        try {
                            cashbackTariffService.calculateCashback(accountId, startOfLastMonth, endOfLastMonth);
                        } catch (Exception exception) {
                            log.error("Ошибка при расчете кешбека для accountId={}", accountId, exception);
                        }
                    });
                });
            });
        } finally {
            executorService.shutdown();
        }

        log.info("Завершён подсчёте кешбека за период с {} по {}", startOfLastMonth, endOfLastMonth);
    }
}
