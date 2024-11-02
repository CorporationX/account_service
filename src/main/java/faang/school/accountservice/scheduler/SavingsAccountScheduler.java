package faang.school.accountservice.scheduler;

import faang.school.accountservice.model.entity.*;
import faang.school.accountservice.model.enums.AccountType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.service.impl.FreeAccountNumbersServiceImpl;
import faang.school.accountservice.service.impl.SavingsAccountServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SavingsAccountScheduler {
    private final FreeAccountNumbersServiceImpl freeAccountNumbersServiceImpl;
    private final SavingsAccountServiceImpl savingsAccountService;
    private final SavingsAccountRepository savingsAccountRepository;
    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;

    @Scheduled(cron = "0 0 1 * * *")
    @Retryable(backoff = @Backoff(delay = 5000))
    protected void savingsAccountNumberGenerator() {
        freeAccountNumbersServiceImpl.ensureMinimumAccountNumbers(AccountType.SAVINGS, 100);
    }

    @Scheduled(cron = "0 * * * * *")
    @Retryable(backoff = @Backoff(delay = 5000))
    protected void calculatePercents() {
        // TODO надо что то сделать
        List<SavingsAccount> savingsAccounts = savingsAccountRepository.findAll();
        Map<String, Double> numberRate = new HashMap<>();
        for (int i = 0; i < savingsAccounts.size(); i++) {
            TariffHistory currentTariffHistory = savingsAccounts.get(i).getTariffHistory().get(savingsAccounts.get(i).getTariffHistory().size() - 1);
            Tariff curentTariff = currentTariffHistory.getTariff();
            SavingsAccountRate savingsAccountRate = curentTariff.getSavingsAccountRates().get(curentTariff.getSavingsAccountRates().size() - 1);
            double currentRate = savingsAccountRate.getRate();
            numberRate.put(savingsAccounts.get(i).getAccountNumber(), currentRate);
        }
        for (var entry : numberRate.entrySet()) {
            Account account = accountRepository.findAccountByNumber(entry.getKey()).orElseGet(() -> {
                log.info("Account with number {} not found", entry.getKey());
                throw new EntityNotFoundException("Account with id " + entry.getKey() + " not found");
            });
            savingsAccountService.calculatePercent(account.getId(), numberRate.get(entry.getValue()));
        }
    }

//    @Scheduled(cron = "0 * * * * *")
//    @Retryable(backoff = @Backoff(delay = 5000))
//    protected void calculatePercents() {
//        // TODO надо что то сделать
//        List<String> accountNumbers = savingsAccountRepository.findAccountNumbers();
//        System.out.println("Account numbers: " + accountNumbers);
//        List<Long>savingsAccountsIds = accountRepository.findSaIdsByAccountNumbers(accountNumbers);
//        System.out.println("Account Ids: " + savingsAccountsIds);
//        savingsAccountsIds.forEach(savingsAccountService.calculatePercent());
//    }

}
