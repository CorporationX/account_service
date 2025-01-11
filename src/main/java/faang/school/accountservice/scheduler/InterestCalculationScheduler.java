package faang.school.accountservice.scheduler;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.TariffHistory;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffHistoryRepository;
import faang.school.accountservice.service.SavingsAccountService;
import faang.school.accountservice.service.TariffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class InterestCalculationScheduler {
    private final SavingsAccountService savingsAccountService;
    private final SavingsAccountRepository savingsAccountRepository;
    private final TariffHistoryRepository tariffHistoryRepository;
    private final TariffService tariffService;

    @Scheduled(cron = "0 0 0 * * *")
    public void calculateInterestForAllAccounts() {
        List<SavingsAccount> accounts = savingsAccountRepository.findAll();
        if (accounts.isEmpty()) {
            log.info("There is NO savings accounts found in the Database");
            return;
        }

        for (int i = 0; i < accounts.size(); i++) {
            List<TariffHistory> tariffHistories = tariffHistoryRepository.findBySavingsAccountId(accounts.get(i).getId());
            Collections.sort(tariffHistories, (t1, t2) -> t2.getAppliedDate().compareTo(t1.getAppliedDate()));
            Double rate = tariffService.getTariff(tariffHistories.get(0).getTariffId()).getRate();
            calculateInterest(accounts.get(i), rate);
            log.info("The calculated interest for all savings accounts has been added to the balances");
        }
    }

    private Double calculateInterest(SavingsAccount savingsAccount, Double rate) {
        return savingsAccount.getAccount().getBalance().getActualBalance().doubleValue() * rate / 365;
    }
}
