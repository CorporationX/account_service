package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.TariffType;
import faang.school.accountservice.exception.BadRequestException;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Evgenii Malkov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SavingsAccountService implements AccountCreationService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsInterestPaymentService savingsInterestPaymentService;
    private final TariffMapper tariffMapper;
    private final TariffRepository tariffRepository;
    @Value("${task.interest-payment.savings-account.batch-size}")
    private int batchSize;

    @Override
    public AccountType getAccountType() {
        return AccountType.SAVINGS;
    }

    @Override
    @Transactional
    public void create(Account data, TariffType type) {
        if (type == null) {
            throw new BadRequestException("The selected tariff id is not specified");
        }
        Tariff tariff = tariffRepository.findTariffByType(type);

        SavingsAccount savingsAccount = SavingsAccount.builder()
                .account(data)
                .accountId(data.getId())
                .lastSuccessPercentDate(LocalDate.now())
                .tariffHistory(List.of(tariff.getId()))
                .version(1L)
                .build();

        savingsAccountRepository.save(savingsAccount);
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockException.class, backoff = @Backoff(delay = 2000))
    public void updateSavingsAccountTariff(long accountId, TariffType tariffType) {
        Tariff tariff = tariffRepository.findTariffByType(tariffType);
        SavingsAccount savingsAccount = savingsAccountRepository.findSavingsAccountByAccountId(accountId);
        if (savingsAccount == null) {
            throw new BadRequestException("Not found savings account with id: " + accountId);
        }

        List<Long> tariffHistory = new ArrayList<>(savingsAccount.getTariffHistory());
        tariffHistory.add(tariff.getId());

        savingsAccount.setTariffHistory(tariffHistory);
    }

    @Transactional(readOnly = true)
    public TariffDto getSavingsAccountTariffByClientId(long clientId) {
        SavingsAccount savingsAccount = savingsAccountRepository
                .findByClientIdAndType(clientId, AccountType.SAVINGS.name())
                .stream()
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Not found savings account for user with id: " + clientId));

        Tariff actualTariff = getActualTariff(savingsAccount.getTariffHistory());
        return tariffMapper.toDto(actualTariff);
    }

    @Transactional(readOnly = true)
    public TariffDto getSavingsAccountTariffByAccountId(long accountId) {
        SavingsAccount savingsAccount = savingsAccountRepository.findSavingsAccountByAccountId(accountId);
        if (savingsAccount == null) {
          throw new BadRequestException("Not found savings account with accountId: " + accountId);
        }

        Tariff actualTariff = getActualTariff(savingsAccount.getTariffHistory());
        return tariffMapper.toDto(actualTariff);
    }

    @Retryable(retryFor = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 3000))
    public void payInterest() {
        log.info("Interest payments on savings accounts have begun: {}", LocalDateTime.now());
        List<SavingsAccount> accountsForPayment = savingsAccountRepository.findSavingsAccountsForPayment(
                AccountStatus.OPEN.name(), AccountType.SAVINGS.name()
        );
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < accountsForPayment.size(); i += batchSize) {
            int end = Math.min(i + batchSize, accountsForPayment.size());
            List<SavingsAccount> batch = accountsForPayment.subList(i, end);
            futures.add(savingsInterestPaymentService.pay(batch));
        }
        CompletableFuture<Void> allOfFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOfFutures.join();
        log.info("Successful execution of payments on all accounts at: {}, for count: {}", LocalDateTime.now(), accountsForPayment.size());
    }

    private Tariff getActualTariff(List<Long> tariffHistory) {
        Long actualTariffId = tariffHistory.get(tariffHistory.size() - 1);
        return findTariffById(actualTariffId);
    }

    private Tariff findTariffById(Long tariffId) {
        return tariffRepository.findById(tariffId)
                .orElseThrow(() -> new BadRequestException("Not found tariff with id: " + tariffId));
    }
}
