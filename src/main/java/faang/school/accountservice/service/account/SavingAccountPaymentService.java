package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.account.SavingAccount;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.repository.account.SavingAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SavingAccountPaymentService {
    private final SavingAccountRepository savingAccountRepository;
    private final AccountRepository accountRepository;

    @Async("savingAccountPayoffInterestTaskExecutor")
    @Retryable(retryFor = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 3000))
    public void payOffInterests(List<SavingAccount> accounts) {
        LocalDateTime now = LocalDateTime.now();
        for (var account : accounts) {
            account.setIncreasedAt(now);
            BigDecimal rate = account.getTariff().getRate();
            BigDecimal oldBalance = account.getAccount().getBalance();
            BigDecimal newBalance = oldBalance.add(oldBalance.multiply(rate));
            account.getAccount().setBalance(newBalance);
            log.info("Balance increased for saving account '{}'", account.getAccount().getPaymentNumber());
        }
        accountRepository.saveAll(accounts.stream().map(SavingAccount::getAccount).toList());
        savingAccountRepository.saveAll(accounts);
    }

    @Recover
    public void recoverException(Exception e) {
        log.error("Pay off interest error", e);
    }
}
