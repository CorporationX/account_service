package faang.school.accountservice.service.savings;

import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.enums.InvoiceType;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.service.FreeAccountNumberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavingsAccountScheduler {

    private final SavingsAccountRepository savingsAccountRepository;
    private final FreeAccountNumberService freeAccountNumberService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void generateAccountNumbersForSavingsAccounts() {
        for (InvoiceType invoiceType : InvoiceType.values()) {
            if (invoiceType == InvoiceType.SAVINGS) {
                freeAccountNumberService.createCounterForAccountType(invoiceType);
                log.info("Генерация номеров для накопительных счетов завершена для типа: {}", invoiceType);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void calculateInterest() {
        List<SavingsAccount> accounts = savingsAccountRepository.findAll();
        accounts.parallelStream().forEach(this::applyInterest);
    }

    @Transactional
    @Retryable(value = {RuntimeException.class}, maxAttempts = 3)
    public void applyInterest(SavingsAccount savingsAccount) {
        LocalDateTime lastInterestDate = savingsAccount.getLastInterestDate();

        if (lastInterestDate == null || lastInterestDate.isBefore(LocalDateTime.now().minusDays(1))) {
            BigDecimal interest = calculateInterestAmount(savingsAccount);
            savingsAccount.setBalance(savingsAccount.getBalance().add(interest));
            savingsAccount.setLastInterestDate(LocalDateTime.now());
            savingsAccountRepository.save(savingsAccount);
            log.info("Проценты начислены на счет id {}: {}", savingsAccount.getId(), interest);
        } else {
            log.info("Проценты еще не начислялись для счета id {}", savingsAccount.getId());
        }
    }

    private BigDecimal calculateInterestAmount(SavingsAccount savingsAccount) {
        BigDecimal interestRate = new BigDecimal("0.05");
        return savingsAccount.getBalance()
                .multiply(interestRate)
                .divide(new BigDecimal("365"), RoundingMode.HALF_UP);
    }

    @Transactional
    public void withdraw(Long accountId, BigDecimal amount) {
        SavingsAccount savingsAccount = savingsAccountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Счет не найден"));

        if (savingsAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Недостаточно средств на счете");
        }

        savingsAccount.setBalance(savingsAccount.getBalance().subtract(amount));
        savingsAccountRepository.save(savingsAccount);
        log.info("Снятие средств с счета id {} на сумму {}", accountId, amount);
    }

    @Transactional
    public void deposit(Long accountId, BigDecimal amount) {
        SavingsAccount savingsAccount = savingsAccountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Счет не найден"));

        savingsAccount.setBalance(savingsAccount.getBalance().add(amount));
        savingsAccountRepository.save(savingsAccount);
        log.info("Пополнение счета id {} на сумму {}", accountId, amount);
    }
}
