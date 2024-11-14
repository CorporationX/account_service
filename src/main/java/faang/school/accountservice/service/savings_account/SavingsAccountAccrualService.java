package faang.school.accountservice.service.savings_account;

import faang.school.accountservice.entity.savings_account.SavingsAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
@RequiredArgsConstructor
@Service
public class SavingsAccountAccrualService {
    private final SavingsAccountService savingsAccountService;
    private final ExecutorService calculateAccrualsExecutorService;

    public void makeAccruals() {
        LocalDate now = LocalDate.now();

        List<SavingsAccount> savingsAccounts = savingsAccountService.getAllActive();

        savingsAccounts.forEach(savingsAccount -> calculateAccrualsExecutorService.execute(() -> {
            try {
                savingsAccountService.accrueBalanceForSavingsAccount(savingsAccount, now);
            } catch (Exception e) {
                log.error("Failed to accrue amount for savings account {}", savingsAccount.getId(), e);
            }
        }));
    }
}
