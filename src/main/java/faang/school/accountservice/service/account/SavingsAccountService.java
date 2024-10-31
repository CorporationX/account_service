package faang.school.accountservice.service.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.executor.ExecutorServiceConfig;
import faang.school.accountservice.dto.account.SavingsAccountCreatedDto;
import faang.school.accountservice.dto.account.SavingsAccountDto;
import faang.school.accountservice.dto.tariff.TariffRequestDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.SavingsAccount;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.mapper.account.SavingsAccountMapper;
import faang.school.accountservice.repository.account.SavingsAccountRepository;
import faang.school.accountservice.service.tariff.TariffService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final ExecutorServiceConfig executorServiceConfig;
    private final SavingsAccountMapper savingsAccountMapper;
    private final AccountService accountService;
    private final TariffService tariffService;
    private final ObjectMapper objectMapper;

    @Transactional
    public SavingsAccountDto openSavingsAccount(SavingsAccountCreatedDto savingsAccountCreatedDto) {
        log.info("start openSavingsAccount with dto - {}", savingsAccountCreatedDto.toString());
        Account account = accountService.getAccount(savingsAccountCreatedDto.getAccountId());
        Tariff tariff = tariffService.getTariffByTariffType(savingsAccountCreatedDto.getTariffType());

        SavingsAccount savingsAccount = SavingsAccount.builder()
                .account(account)
                .owner(account.getOwner())
                .tariff(tariff)
                .balance(BigDecimal.ZERO)
                .number(UUID.randomUUID().toString())
                .build();

        updateTariffHistory(tariff, savingsAccount);

        SavingsAccount createdSavingsAccount = savingsAccountRepository.save(savingsAccount);
        log.info("finish openSavingsAccount with entity: {}", createdSavingsAccount);

        return savingsAccountMapper.toDto(createdSavingsAccount);
    }

    public SavingsAccountDto getSavingsAccountById(Long savingsAccountId) {
        log.info("start getSavingsAccountById with id - {}", savingsAccountId);
        SavingsAccount existedSavingsAccount = savingsAccountRepository.findById(savingsAccountId)
                .orElseThrow(() -> new NoSuchElementException("SavingsAccount with id - "
                        + savingsAccountId + " does not exist"));
        log.info("finish getSavingsAccountById with entity: {}", existedSavingsAccount.toString());

        return savingsAccountMapper.toDto(existedSavingsAccount);
    }

    public SavingsAccountDto getSavingsAccountByOwnerId(Long ownerId) {
        log.info("start getSavingsAccountByOwnerId with ownerId - {}", ownerId);
        SavingsAccount existedSavingsAccount = savingsAccountRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new NoSuchElementException("SavingsAccount at ownerId - "
                + ownerId + " does not exist"));
        log.info("finish getSavingsAccountByOwnerId with entity: {}", existedSavingsAccount.toString());

        return savingsAccountMapper.toDto(existedSavingsAccount);
    }

    @Async("executor")
    public void calculatePercents() {
        log.info("start calculatePercents, thread name: {}", Thread.currentThread().getName());
        List<SavingsAccount> existedSavingsAccounts = savingsAccountRepository.findAllReadyToInterestCalculation();

        CompletableFuture<Void> calculatedSavingsAccount = CompletableFuture.runAsync(
                () -> calculate(existedSavingsAccounts), executorServiceConfig.executor());

        calculatedSavingsAccount.thenAccept(result -> savingsAccountRepository.saveAll(existedSavingsAccounts));
        log.info("finish calculatePercents, thread name: {}", Thread.currentThread().getName());
    }

    private void calculate(List<SavingsAccount> savingsAccounts) {
        log.info("start calculate, thread name: {}", Thread.currentThread().getName());
        savingsAccounts.forEach(savingsAccount -> {
            log.info("Thread name: {} take Account id: {}", Thread.currentThread().getName(), savingsAccount.getId());
            BigDecimal updatedBalance = savingsAccount.getBalance()
                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(savingsAccount.getTariff().getRate().getInterestRate()));

            log.info("Thread name: {} save Account id: {}", Thread.currentThread().getName(), savingsAccount.getId());
            savingsAccount.setBalance(updatedBalance);
        });
    }

    private void updateTariffHistory(Tariff tariff, SavingsAccount savingsAccount) {
        try {
            List<String> tariffs = new ArrayList<>();

            if (savingsAccount.getTariffHistory() != null) {
                tariffs = objectMapper.readValue(savingsAccount.getTariffHistory(), new TypeReference<>() {
                });
            }
            tariffs.add(tariff.getTariffType());

            savingsAccount.setTariffHistory(objectMapper.writeValueAsString(tariffs));
        } catch (JsonProcessingException ex) {
            log.info("Failed to updated tariff in savingsAccount - {}", savingsAccount);
            throw new RuntimeException(ex);
        }
    }
}
