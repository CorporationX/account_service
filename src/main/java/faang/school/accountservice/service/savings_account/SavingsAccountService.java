package faang.school.accountservice.service.savings_account;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.entity.savings_account.SavingsAccount;
import faang.school.accountservice.entity.savings_account.TariffToSavingAccountBinding;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.entity.tariff.TariffRate;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.balance.BalanceService;
import faang.school.accountservice.service.tariff.TariffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static faang.school.accountservice.enums.account.AccountStatus.ACTIVE;

@Slf4j
@RequiredArgsConstructor
@Service
public class SavingsAccountService {
    private final AccountService accountService;
    private final BalanceService balanceService;
    private final TariffService tariffService;
    private final SavingsAccountRepository savingsAccountRepository;

    @Transactional
    public SavingsAccount openSavingsAccount(SavingsAccount savingsAccount, UUID accountId, Long tariffId, BigDecimal amount) {
        Account account = accountService.getAccountById(accountId);
        savingsAccount.setAccount(account);

        Balance createdBalance = balanceService.createOrGetBalanceWithAmount(account, amount);

        Tariff tariff = tariffService.findById(tariffId);
        TariffToSavingAccountBinding tariffToSavingAccountBinding = build(tariff, savingsAccount);
        savingsAccount.getTariffToSavingAccountBindings().add(tariffToSavingAccountBinding);

        setAdditionalData(savingsAccount, tariff, createdBalance);

        return savingsAccountRepository.save(savingsAccount);
    }

    @Transactional(readOnly = true)
    public SavingsAccount getById(UUID id) {
        SavingsAccount savingsAccount = savingsAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SavingsAccount.class, id));
        setAdditionalData(savingsAccount);
        return savingsAccount;
    }

    @Transactional(readOnly = true)
    public List<SavingsAccount> getAllActive() {
        return savingsAccountRepository.findByAccount_Status(ACTIVE);
    }

    @Transactional
    public SavingsAccount updateTariff(UUID savingsAccountId, Long tariffId) {
        SavingsAccount savingsAccount = savingsAccountRepository.findById(savingsAccountId)
                .orElseThrow(() -> new ResourceNotFoundException(SavingsAccount.class, savingsAccountId));

        Tariff tariff = tariffService.findById(tariffId);
        TariffToSavingAccountBinding tariffToSavingAccountBinding = build(tariff, savingsAccount);
        savingsAccount.getTariffToSavingAccountBindings().add(tariffToSavingAccountBinding);

        setAdditionalData(savingsAccount);

        return savingsAccountRepository.save(savingsAccount);
    }

    @Transactional(readOnly = true)
    public double getCurrentRate(UUID savingsAccountId) {
        return savingsAccountRepository.getCurrentRate(savingsAccountId);
    }

    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 2,
            backoff = @Backoff(delay = 10_000))
    @Transactional
    public void accrueBalanceForSavingsAccount(SavingsAccount savingsAccount, LocalDate now) {
        accrueBalance(savingsAccount);
        savingsAccount.setLastCalculationDate(now);
        savingsAccountRepository.save(savingsAccount);
    }

    public void accrueBalance(SavingsAccount savingsAccount) {
        UUID accountId = savingsAccount.getAccount().getId();
        double currentRate = getCurrentRate(savingsAccount.getId());
        double rateWithBonus = currentRate + savingsAccount.getBonus();
        // TODO rateWithBonus > 0 need to check
        balanceService.multiplyCurrentBalance(accountId, rateWithBonus);
    }

    private void setAdditionalData(SavingsAccount savingsAccount, Tariff tariff, Balance balance) {
        List<TariffRate> tariffRates = tariff.getTariffRates().stream()
                .sorted(Comparator.comparing(TariffRate::getCreatedAt).reversed())
                .toList();
        double currentRate = tariffRates.get(0).getRate();

        setCurrentRateAndAmount(savingsAccount, currentRate, balance.getCurrentBalance());
    }

    private void setAdditionalData(SavingsAccount savingsAccount) {
        double currentRate = getCurrentRate(savingsAccount.getId());
        Balance balance = balanceService.findByAccountId(savingsAccount.getAccount().getId());

        setCurrentRateAndAmount(savingsAccount, currentRate, balance.getCurrentBalance());
    }

    private void setCurrentRateAndAmount(SavingsAccount savingsAccount, double currentRate, BigDecimal amount) {
        savingsAccount.setCurrentRate(currentRate);
        savingsAccount.setAmount(amount);
    }

    private TariffToSavingAccountBinding build(Tariff tariff, SavingsAccount savingsAccount) {
        return TariffToSavingAccountBinding.builder()
                .tariff(tariff)
                .savingsAccount(savingsAccount)
                .build();
    }

    public void updateBonusAfterAchievementAccepted(long userId, long points) {
        SavingsAccount savingsAccount = savingsAccountRepository.findByAccountUserId(userId).orElse(null);
        Double currentBonus = savingsAccount.getBonus();
        Double newBonus = currentBonus + points / 1000;
        savingsAccount.setBonus(newBonus);
        savingsAccountRepository.save(savingsAccount);
    }
}
