package faang.school.accountservice.service.savingsaccount;

import faang.school.accountservice.dto.savingsaccount.DepositDto;
import faang.school.accountservice.dto.savingsaccount.OpenSavingsAccountDto;
import faang.school.accountservice.dto.savingsaccount.SavingsAccountResponseDto;
import faang.school.accountservice.dto.savingsaccount.WithdrawDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.exception.InsufficientFundsException;
import faang.school.accountservice.exception.SavingsAccountNotFoundException;
import faang.school.accountservice.exception.TariffNotFoundException;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SavingsAccountServiceImpl implements SavingsAccountService {
    private final SavingsAccountRepository savingsAccountRepository;
    private final AccountRepository accountRepository;
    private final TariffRepository tariffRepository;
    private final SavingsAccountMapper savingsAccountMapper;

    @Override
    public SavingsAccountResponseDto openSavingsAccount(
            OpenSavingsAccountDto accountDto
    ) {
        if (savingsAccountRepository.existsByAccountId(accountDto.getAccountId())) {
            throw new IllegalStateException("Savings account already exists for this account");
        }

        Account account = accountRepository.findById(accountDto.getAccountId())
                .orElseThrow(() -> new SavingsAccountNotFoundException("Account not found"));

        SavingsAccount savingsAccount = new SavingsAccount();

        savingsAccount.setAccount(account);
        savingsAccount.setTariffHistory(new ArrayList<>(List.of(accountDto.getInitialTariffId())));
        savingsAccount.setLastInterestDate(LocalDateTime.now());

        savingsAccountRepository.save(savingsAccount);

        return savingsAccountMapper.toSavingsAccountResponseDto(savingsAccount);
    }

    @Override
    public SavingsAccountResponseDto getSavingsAccountById(Long accountId) {
        SavingsAccount account = savingsAccountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new SavingsAccountNotFoundException("Account not found"));
        return savingsAccountMapper.toSavingsAccountResponseDto(account);
    }

    @Override
    public SavingsAccountResponseDto changeTariff(
            Long savingsAccountId, Long newTariffId
    ) {
        SavingsAccount savingsAccount = savingsAccountRepository.findById(savingsAccountId)
                .orElseThrow(() -> new SavingsAccountNotFoundException("Account not found"));

        if (tariffRepository.existsById(newTariffId)) {
            throw new TariffNotFoundException("not found");
        }

        savingsAccount.addTariffId(newTariffId);
        savingsAccountRepository.save(savingsAccount);

        return savingsAccountMapper.toSavingsAccountResponseDto(savingsAccount);
    }

    @Override
    public SavingsAccountResponseDto deposit(DepositDto depositDto) {
        SavingsAccount savingsAccount = savingsAccountRepository.findById(depositDto.getSavingsAccountId())
                .orElseThrow(() -> new SavingsAccountNotFoundException("Account not found"));

        savingsAccount.setBalance(savingsAccount.getBalance().add(depositDto.getAmount()));
        savingsAccountRepository.save(savingsAccount);
        return savingsAccountMapper.toSavingsAccountResponseDto(savingsAccount);
    }

    @Override
    public SavingsAccountResponseDto withdraw(WithdrawDto withdrawDto) {
        SavingsAccount savingsAccount = savingsAccountRepository.findById(withdrawDto.getSavingsAccountId())
                .orElseThrow(() -> new SavingsAccountNotFoundException("Account not found"));

        BigDecimal amount = withdrawDto.getAmount();

        if (savingsAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        savingsAccount.setBalance(savingsAccount.getBalance().subtract(amount));
        savingsAccountRepository.save(savingsAccount);
        return savingsAccountMapper.toSavingsAccountResponseDto(savingsAccount);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Retryable(
            retryFor = {OptimisticLockException.class},
            maxAttemptsExpression = "${app.optimistic-lock-max-attempts}",
            backoff = @Backoff(delayExpression = "${app.optimistic-lock-backoff-delay}")
    )
    public void calculateDailyInterest() {
        List<SavingsAccount> accounts = savingsAccountRepository.findAll();
        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (SavingsAccount account : accounts) {
            executor.submit(() -> {
                try {
                    calculateInterestForAccount(account);
                } catch (Exception e) {
                    // Log error and continue with other accounts
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void calculateInterestForAccount(SavingsAccount account) {
        Long currentTariffId = account.getCurrentTariffId();
        Tariff tariff = tariffRepository.findById(currentTariffId).get();
        BigDecimal currentRate = tariff.getCurrentRate();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastDate = account.getLastInterestDate() != null
                ? account.getLastInterestDate()
                : account.getCreatedAt();
        long days = ChronoUnit.DAYS.between(lastDate, now);
        if (days <= 0) return;
        BigDecimal dailyRate = currentRate.divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP);
        BigDecimal interest = account.getBalance()
                .multiply(dailyRate)
                .multiply(BigDecimal.valueOf(days))
                .setScale(2, RoundingMode.HALF_UP);

        account.setBalance(account.getBalance().add(interest));
        account.setLastInterestDate(now);
        savingsAccountRepository.save(account);
    }
}
