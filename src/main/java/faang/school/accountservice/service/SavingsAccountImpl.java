package faang.school.accountservice.service;


import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class SavingsAccountImpl implements SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsAccountMapper savingsAccountMapper;
    private final AccountRepository accountRepository;
    private final FreeAccountNumbersService freeAccountNumbersService;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final SavingsAccountInterest savingsAccountInterest;

    @Value(value = "${poolThreads.poolForAccrual}")
    private Integer poolThreads;

    @Override
    public SavingsAccountDto openSavingsAccount(SavingsAccountDto savingsAccountDto) {
        SavingsAccount account = savingsAccountMapper.toEntity(savingsAccountDto);
        account.getAccount().setNumber(prepareNumberForSavingsAccount());
        savingsAccountRepository.save(account);
        return savingsAccountDto;
    }

    @Override
    public SavingsAccountDto getSavingsAccountById(Long id) {
        SavingsAccount savingsAccount = savingsAccountRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Savings account not found with id " + id));

        return toSavingsAccountDto(savingsAccount);
    }

    @Override
    public SavingsAccountDto getSavingsAccountByUserId(Long userId) {
        SavingsAccount savingsAccount = accountRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Savings account not found with id " + userId)).getSavingsAccount();

        return toSavingsAccountDto(savingsAccount);
    }

    private SavingsAccountDto toSavingsAccountDto(SavingsAccount savingsAccount) {
        Tariff currentTariff = savingsAccount.getTariff();
        List<BigDecimal> tariffHistory = savingsAccount.getTariff().getBettingHistory();
        String number = prepareNumberForSavingsAccount();

        return SavingsAccountDto.builder()
                .accountId(savingsAccount.getId())
                .number(number)
                .tariffId(currentTariff.getId())
                .bettingHistory(tariffHistory)
                .build();
    }

    @Scheduled(cron = "${scheduled.task.cronForAccrual}")
    @Retryable(maxAttempts = 3)
    private void interestAccrual() {
        List<SavingsAccount> accounts = savingsAccountRepository.findAll();
        ExecutorService executor = Executors.newFixedThreadPool(poolThreads);

        for (SavingsAccount savingsAccount : accounts) {
            executor.submit(() -> savingsAccountInterest.processInterestAccrual(savingsAccount));
        }

        executor.shutdown();
    }

    @Scheduled(cron = "${scheduled.task.cronForGenerateNumbers}")
    private void generateNumbers() {
        List<String> numbers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            numbers.add(freeAccountNumbersService.generateNumberByType(AccountType.SAVINGS_ACCOUNT));
        }

        freeAccountNumbersService.saveNumbers(AccountType.SAVINGS_ACCOUNT, numbers);
    }

    private String prepareNumberForSavingsAccount() {
        return freeAccountNumbersRepository.getFreeAccountNumberByType(AccountType.SAVINGS_ACCOUNT.name()).orElseThrow(
                () -> new EntityNotFoundException("Number not found"));
    }
}
