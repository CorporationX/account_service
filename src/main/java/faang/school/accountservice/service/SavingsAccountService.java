package faang.school.accountservice.service;

import faang.school.accountservice.dto.savingsAccount.BalanceDto;
import faang.school.accountservice.dto.savingsAccount.SavingsAccountRequestDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.repository.SavingsAccountRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SavingsAccountService {
    private final AccountService accountService;
    private final TariffService tariffService;
    private final SavingsAccountRepository savingsAccountRepository;

    @Transactional
    public SavingsAccount openSavingsAccount(SavingsAccountRequestDto savingsAccount) {
        Account account = accountService.getAccountById(savingsAccount.accountId());
        Tariff tariff = tariffService.getTariff(savingsAccount.tariffName());

        SavingsAccount createSavingsAccount = SavingsAccount.builder()
            .account(account)
            .tariff(tariff)
            .tariffHistory(new ArrayList<>())
            .build();

        return savingsAccountRepository.save(createSavingsAccount);
    }

    @Transactional(readOnly = true)
    public SavingsAccount getById(UUID id) {
        return savingsAccountRepository.findById(id)
            .orElseThrow(
                () -> new NoSuchElementException("SavingsAccount not found with id " + id));
    }

    @Transactional(readOnly = true)
    public SavingsAccount getByAccountId(Long accountId) {
        return savingsAccountRepository.findByAccountId(accountId)
            .orElseThrow(
                () -> new NoSuchElementException(
                    "SavingsAccount not found with accountId " + accountId));
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Async("interestСalculation")
    public void applyInterest() {
        LocalDate today = LocalDate.now();

        List<SavingsAccount> savingsAccountList =
            savingsAccountRepository.findByLastInterestDateBefore(today);

        savingsAccountList.parallelStream().forEach(savingsAccount -> {
            try {
                calculateAndApplyInterest(savingsAccount, today);
            } catch (Exception e) {
                log.error("Error processing savingsAccount {}: {}", savingsAccount.getId(),
                    e.getMessage());
            }
        });
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public void calculateAndApplyInterest(SavingsAccount savingsAccount, LocalDate date) {
        BigDecimal percentages = savingsAccount.getBalance()
            .multiply(BigDecimal.valueOf(savingsAccount.getTariff().getRate()))
            .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

        BigDecimal newBalance = savingsAccount.getBalance().add(percentages);

        savingsAccount.setBalance(newBalance);
        savingsAccount.setLastInterestDate(date);
        savingsAccountRepository.save(savingsAccount);
    }

    @Transactional
    public SavingsAccount deposit(BalanceDto balanceDto) {
        SavingsAccount savingsAccount = getByAccountId(balanceDto.accountId());
        BigDecimal balance = savingsAccount.getBalance().add(balanceDto.amount());
        savingsAccount.setBalance(balance);
        return savingsAccountRepository.save(savingsAccount);
    }

    @Transactional
    public SavingsAccount withdraw(BalanceDto balanceDto) {
        SavingsAccount savingsAccount = getByAccountId(balanceDto.accountId());

        if (savingsAccount.getBalance().compareTo(balanceDto.amount()) < 0) {
            throw new IllegalArgumentException("Not enough funds");
        }

        BigDecimal balance = savingsAccount.getBalance().subtract(balanceDto.amount());
        savingsAccount.setBalance(balance);
        return savingsAccountRepository.save(savingsAccount);
    }
}
