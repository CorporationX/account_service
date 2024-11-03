package faang.school.accountservice.scheduler;

import faang.school.accountservice.service.CashbackTariffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CashbackAccrualScheduler {

    private final CashbackTariffService cashbackTariffService;

    @Scheduled(cron = "${cashback.cron}")
    public void cashbackAccrual() {
        log.info("Initiating cashback accrual process: dispatching tasks for all accounts.");

        int dispatchedAccounts = cashbackTariffService.earnCashbackOnExpensesAllAccounts();
        log.info("Cashback accrual tasks dispatched. Total accounts with cashback tasks queued: {}", dispatchedAccounts);

        log.info("Cashback accrual process completed. Tasks are still in progress.");
    }
}