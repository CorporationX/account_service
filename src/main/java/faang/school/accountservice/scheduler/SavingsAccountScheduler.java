package faang.school.accountservice.scheduler;

import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.service.SavingsAccountUpdateAsyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SavingsAccountScheduler {
    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsAccountUpdateAsyncService savingsAccountAsyncService;

    @Scheduled(cron = "${app.scheduling.balance_update.cron}")
    public void calculateAndUpdateInterest(){
        savingsAccountRepository.findAll().forEach(savingsAccountAsyncService::updateInterestAsync);
    }
}
