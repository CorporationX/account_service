package faang.school.accountservice.service;

import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.exception.SavingsAccountHasBeenUpdateException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.model.SavingsAccountTariff;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.enumeration.TariffType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.validator.SavingsAccountValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SavingsAccountService {
    private final AccountRepository accountRepository;
    private final SavingsAccountRepository repository;
    private final TariffRepository tariffRepository;
    private final BalanceService balanceService;
    private final SavingsAccountValidator savingsAccountValidator;

    @Transactional
    public SavingsAccount create(Account account, SavingsAccount savingsAccount, TariffType tariffType) {
        Account createdAccount = accountRepository.save(account);
        savingsAccount.setAccount(createdAccount);

        Tariff tariff = tariffRepository.findByTariffType(tariffType).orElse(null);

        SavingsAccountTariff savingsAccountTariff = SavingsAccountTariff.builder()
                .tariff(tariff)
                .savingsAccount(savingsAccount)
                .build();

        savingsAccount.getSavingsAccountTariffs().add(savingsAccountTariff);
        return repository.save(savingsAccount);
    }

    @Transactional(readOnly = true)
    public SavingsAccount findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings Account", id));
    }

    @Transactional
    public SavingsAccount updateTariff(Long savingsAccountId, TariffType tariffType) {
        SavingsAccount foundSavingsAccount = findById(savingsAccountId);

        Tariff foundTariff = tariffRepository.findByTariffType(tariffType)
                .orElseThrow(() -> new ResourceNotFoundException("Tariff", tariffType));

        SavingsAccountTariff savingsAccountTariff = SavingsAccountTariff.builder()
                .tariff(foundTariff)
                .savingsAccount(foundSavingsAccount)
                .build();

        foundSavingsAccount.getSavingsAccountTariffs().add(savingsAccountTariff);

        try {
            repository.save(foundSavingsAccount);
            repository.flush();
        } catch (OptimisticLockingFailureException exception) {
            throw new SavingsAccountHasBeenUpdateException(foundSavingsAccount.getId());
        }

        return foundSavingsAccount;
    }

    @Transactional(readOnly = true)
    public List<SavingsAccount> getAll() {
        return repository.findAll();
    }

    @Transactional
    public List<SavingsAccount> saveAll(List<SavingsAccount> savingsAccounts) {
        return repository.saveAll(savingsAccounts);
    }

    @Transactional
    public void makeAccruals() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        LocalDate now = LocalDate.now();
        List<SavingsAccount> savingsAccounts = getAll();

        List<CompletableFuture<Void>> futures = savingsAccounts.stream()
                .map(savingsAccount ->
                        CompletableFuture.runAsync(() ->
                                {
                                    Balance updatedBalance = accrueBalance(savingsAccount);
                                    if (updatedBalance != null) {
                                        savingsAccount.setLastCalculationDate(now);
                                    }
                                }, executorService
                        )
                )
                .toList();

        CompletableFuture
                .allOf(futures.toArray(CompletableFuture[]::new))
                .join();

        saveAll(savingsAccounts);
    }

    @Retryable(
            recover = "recoverAccrueBalance",
            retryFor = {Exception.class},
            maxAttempts = 2,
            backoff = @Backoff(delay = 60_000))
    private Balance accrueBalance(SavingsAccount savingsAccount) {
        Balance balance = savingsAccount.getAccount().getBalance();
        Double currentRate = savingsAccount.getCurrentTariff().getCurrentRate();
        Double multiplyValue = currentRate / 100;
        return balanceService.multiplyCurrentBalance(balance.getId(), multiplyValue);
    }

    @Recover
    public Balance recoverAccrueBalance(Exception e, SavingsAccount savingsAccount) {
        log.error("Failed to make accrual for SavingsAccount {}", savingsAccount.getAccount().getId());
        return null;
    }
}
