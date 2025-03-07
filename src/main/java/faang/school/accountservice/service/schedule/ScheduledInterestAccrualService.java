package faang.school.accountservice.service.schedule;

import faang.school.accountservice.model.savings_account.SavingsAccount;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.service.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledInterestAccrualService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final ExecutorService interestPool;
    private final SavingsAccountService savingsAccountService;

    @Scheduled(cron = "${config.schedule-daily-interest-accrual-cron}")
    public void scheduleDailyInterestAccrual() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<SavingsAccount> page;

        List<CompletableFuture<Void>> tasks = new ArrayList<>();

        do {
            page = savingsAccountRepository.findAll(pageRequest);

            for (SavingsAccount account : page) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
                        savingsAccountService.applyAccruedInterest(account.getId()),
                        interestPool
                );
                tasks.add(future);
            }

            pageRequest = pageRequest.next();
        } while (page.hasNext());

        tasks.forEach(CompletableFuture::join);
        log.info("All interests are calculated successfully!");
    }
}
