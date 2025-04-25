package faang.school.accountservice.service;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.SavingsAccountResponse;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.entity.tariff.TariffHistory;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.SavingsAccountDuplicateException;
import faang.school.accountservice.exception.TariffNotFoundException;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsAccountMapper savingsAccountMapper;
    private final AccountRepository accountRepository;
    private final UserContext userContext;
    private final TariffRepository tariffRepository;
    private final Executor tariffRatesCalculator;
    private final ProcessorSavingsAccountService processorSavingsAccountService;

    @Value("${thread-pool-setting.tariff-rate-calculator.hours}")
    private int hours;

    @Transactional
    public SavingsAccountResponse openSavingsAccount(Long tariffId) {
        Long ownerId = userContext.getUserId();
        Account account = getAccountByOwnerId(ownerId);
        Tariff tariff = getTariff(tariffId);
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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateTariffOnSavingsAccount(Long accountId, Long tariffId) {
        SavingsAccount savingsAccount = getSavingsAccount(accountId);
        Tariff tariff = getTariff(tariffId);

        List<TariffHistory> tariffHistory = new ArrayList<>(savingsAccount.getTariffHistory());
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
                        () -> processorSavingsAccountService.processSingleAccount(account), tariffRatesCalculator))
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

    private Tariff getTariff(Long tariffId) {
        return tariffRepository.findById(tariffId).orElseThrow(
                () -> new TariffNotFoundException("Tariff with id %d not found", tariffId));
    }
}
