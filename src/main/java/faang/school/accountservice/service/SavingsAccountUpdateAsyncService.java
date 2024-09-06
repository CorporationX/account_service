package faang.school.accountservice.service;

import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.repository.SavingsAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavingsAccountUpdateAsyncService {
    private final SavingsAccountRepository savingsAccountRepository;

    @Async("taskExecutor")
    public void updateInterestAsync(SavingsAccount account){
        try {
            account.setLastInterestCalculationDate(LocalDateTime.now());
            savingsAccountRepository.save(account);
        } catch (Exception e) {
            log.error("Error updating interest for account: {}", account.getId(), e);
        }
    }
}
