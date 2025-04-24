package faang.school.accountservice.service;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.SavingsAccountResponse;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.entity.tariff.TariffHistory;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.exception.RetryableException;
import faang.school.accountservice.exception.SavingsAccountDuplicateException;
import faang.school.accountservice.exception.TariffNotFoundException;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingsAccountService {

    private static final int MULTIPLIER_BY_SCALE = 4;
    private static final int AMOUNT_BY_SCALE = 2;
    private static final int DIVIDER = 100;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final int RETRY_DELAY = 500;
    private static final int RETRY_MULTIPLIER = 3;

    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsAccountMapper savingsAccountMapper;
    private final AccountRepository accountRepository;
    private final UserContext userContext;
    private final TariffRepository tariffRepository;
    private final Executor tariffRatesCalculator;

    @Value("${thread-pool-setting.tariff-rate-calculator.hours}")
    private int hours;

    @Transactional
    public SavingsAccountResponse openSavingsAccount(Long tariffId) {
        Long ownerId = userContext.getUserId();
        Account account = getAccountByOwnerId(ownerId);
        Tariff tariff = tariffRepository.findById(tariffId).orElseThrow(
                () -> new TariffNotFoundException("Tariff with id %d not found", tariffId));
        SavingsAccount savingsAccount = createSavingsAccount(account);

        List<TariffHistory> tariffHistory = new ArrayList<>();
        tariffHistory.add(createTariffHistory(tariff, savingsAccount));
        savingsAccount.setTariffHistory(tariffHistory);

        try {
            savingsAccount = savingsAccountRepository.save(savingsAccount);
            log.info("Opened new savings account. {}", savingsAccount.getAccountNumber());
            return savingsAccountMapper.toDto(savingsAccount);
        } catch (DataIntegrityViolationException ex) {
            throw new SavingsAccountDuplicateException("Savings account on owner with id %d already exists", ownerId);
        }
    }

    public void updateTariffOnSavingsAccount(Long accountId, Long tariffId) {
        SavingsAccount savingsAccount = savingsAccountRepository.findById(accountId).orElseThrow(
                () -> new AccountNotFoundException("Savings account with id %d not found", accountId));
        Tariff tariff = tariffRepository.findById(tariffId).orElseThrow(
                () -> new TariffNotFoundException("Tariff with id %d not found", tariffId));

        List<TariffHistory> tariffHistory = savingsAccount.getTariffHistory();
        tariffHistory.add(createTariffHistory(tariff, savingsAccount));
        savingsAccount.setTariffHistory(tariffHistory);

        savingsAccountRepository.save(savingsAccount);
    }

    @Async("tariffRatesCalculator")
    public void recalculateTariffRates() {
        LocalDateTime dayAgoTime = LocalDateTime.now().minusHours(hours);
        List<SavingsAccount> accounts = savingsAccountRepository.findAllByLastInterestAccrualAtIsBefore(dayAgoTime);

        List<CompletableFuture<Void>> futures = accounts.stream()
                .map(account -> CompletableFuture.runAsync(
                        () -> processSingleAccount(account), tariffRatesCalculator))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public SavingsAccountResponse getSavingsAccountById(Long id) {
        return getSavingsAccountResponse(id);
    }

    public SavingsAccountResponse getSavingsAccountByOwnerId(Long ownerId) {
        Account account = getAccountByOwnerId(ownerId);
        Long accountId = account.getId();

        return getSavingsAccountResponse(accountId);
    }

    private SavingsAccountResponse getSavingsAccountResponse(Long accountId) {
        SavingsAccount savingsAccount = getSavingsAccount(accountId);
        SavingsAccountResponse response = savingsAccountMapper.toDto(savingsAccount);

        String typeName = response.getActiveTariff();
        BigDecimal latestRate = tariffRepository.findLatestRateByTariffTypeName(typeName)
                .orElseThrow(() -> new TariffNotFoundException("Tariff %s not found", typeName));

        response.setActiveTariffRate(latestRate.toString());
        return response;
    }

    @Retryable(
            retryFor = RetryableException.class,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER)
    )
    private void processSingleAccount(SavingsAccount account) {
        try {
            String activeTypeName = savingsAccountRepository.findLatestTariffTypeNameByAccountId(account.getAccountId())
                    .orElseThrow(() -> new TariffNotFoundException("Tariff not found"));
            BigDecimal activeRate = tariffRepository.findLatestRateByTariffTypeName(activeTypeName)
                    .orElseThrow(() -> new NotFoundException("Rate not found"));

            BigDecimal balance = account.getBalance();
            BigDecimal rateMultiplier = activeRate.divide(
                    BigDecimal.valueOf(DIVIDER), MULTIPLIER_BY_SCALE, ROUNDING_MODE);
            BigDecimal interestAmount = balance.multiply(rateMultiplier).setScale(AMOUNT_BY_SCALE, ROUNDING_MODE);
            BigDecimal newBalance = balance.add(interestAmount);
            account.setBalance(newBalance);

            savingsAccountRepository.save(account);
            log.debug("Savings account {} balance recalculated", account.getAccountNumber());
        } catch (DataAccessException | TransactionException e) {
            log.warn("Retryable error processing account {}: {}", account.getAccountNumber(), e.getMessage());
            throw new RetryableException(e.getMessage(), e);
        } catch (Exception ex) {
            throw new CompletionException("Error processing account " + account.getAccountNumber(), ex);
        }
    }

    private SavingsAccount createSavingsAccount(Account account) {
        return SavingsAccount.builder()
                .account(account)
                .accountNumber(UUID.randomUUID().toString())
                .build();
    }

    private TariffHistory createTariffHistory(Tariff tariff, SavingsAccount savingsAccount) {
        return TariffHistory.builder()
                .tariff(tariff)
                .savingsAccount(savingsAccount)
                .build();
    }

    private Account getAccountByOwnerId(Long ownerId) {
        return accountRepository.findByOwnerId(ownerId).orElseThrow(
                () -> new AccountNotFoundException("Account with owner %d not found", ownerId));
    }

    private SavingsAccount getSavingsAccount(Long accountId) {
        return savingsAccountRepository.findById(accountId).orElseThrow(
                () -> new AccountNotFoundException("Savings account with id %d not found", accountId));
    }
}
