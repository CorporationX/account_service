package faang.school.accountservice.service;

import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.repository.SavingsAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Evgenii Malkov
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SavingsInterestPaymentService {

    private final SavingsAccountRepository savingsAccountRepository;

    @Async("schedulerExecutor")
    @Transactional
    public CompletableFuture<Void> pay(List<SavingsAccount> batch) {
        LocalDate today = LocalDate.now();
        for (SavingsAccount account : batch) {
            account.setLastSuccessPercentDate(today);
        }
        savingsAccountRepository.saveAll(batch);
        return CompletableFuture.completedFuture(null);
    }
}
