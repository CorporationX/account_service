package faang.school.accountservice.service.impl;

import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.model.dto.SavingsAccountDto;
import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.entity.SavingsAccount;
import faang.school.accountservice.model.entity.Tariff;
import faang.school.accountservice.model.entity.TariffHistory;
import faang.school.accountservice.repository.*;
import faang.school.accountservice.service.SavingsAccountService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavingsAccountServiceImpl implements SavingsAccountService {
    private final SavingsAccountMapper savingsAccountMapper;
    private final AccountRepository accountRepository;
    private final SavingsAccountRepository savingsAccountRepository;
    private final TariffRepository tariffRepository;
    private final TariffHistoryRepository tariffHistoryRepository;
    private final SavingsAccountRateRepository savingsAccountRateRepository;
    private final BalanceServiceImpl balanceService;
    private final DataSourceTransactionManagerAutoConfiguration dataSourceTransactionManagerAutoConfiguration;

    @Transactional
    @Override
    public SavingsAccountDto openSavingsAccount(SavingsAccountDto savingsAccountDto) {
        Tariff tariff = tariffRepository.findById(savingsAccountDto.getTariffId()).orElseGet(() -> {
            log.info("Tariff with id {} not found", savingsAccountDto.getTariffId());
            throw new EntityNotFoundException("Tariff with id " + savingsAccountDto.getTariffId() + " not found");
        });

        Account account = accountRepository.findById(savingsAccountDto.getAccountId()).orElseGet(() -> {
            log.info("Account with id {} not found", savingsAccountDto.getAccountId());
            throw new EntityNotFoundException("Account with id " + savingsAccountDto.getAccountId() + " not found");
        });

        SavingsAccount savingsAccount = SavingsAccount.builder()
                .account(account)
                .accountNumber(account.getNumber())
                .build();
        savingsAccount = savingsAccountRepository.save(savingsAccount);

        TariffHistory tariffHistory = TariffHistory.builder()
                .savingsAccount(savingsAccount)
                .tariff(tariff)
                .build();
        tariffHistoryRepository.save(tariffHistory);
        SavingsAccountDto resultDto = savingsAccountMapper.savingsAccountToSavingsAccountDto(savingsAccount);
        resultDto.setTariffId(tariff.getId());
        return resultDto;
    }

    @Override
    public SavingsAccountDto getSavingsAccount(Long id) {
        SavingsAccountDto savingsAccountDto = savingsAccountRepository.findSavingsAccountWithDetails(id);
        if (savingsAccountDto == null) {
            log.info("SavingsAccount with id {} not found", id);
            throw new EntityNotFoundException("SavingsAccount with id " + id + " not found");
        }
        return savingsAccountDto;
    }

    @Override
    public List<SavingsAccountDto> getSavingsAccountByUserId(Long userId) {
        List<String> numbers = accountRepository.findNumbersByUserId(userId);
        if (numbers.isEmpty()) {
            throw new EntityNotFoundException("Accounts with user id " + userId + " not found");
        }

        List<SavingsAccount> savingsAccounts = savingsAccountRepository.findSaByAccountNumbers(numbers);
        List<SavingsAccountDto> savingsAccountDtos = savingsAccountMapper.toDtos(savingsAccounts);
        savingsAccountDtos
                .forEach(saDto -> {
                    Long id = tariffHistoryRepository.findLatestTariffIdBySavingsAccountId(saDto.getId()).orElseGet(() -> {
                        log.info("Tariff history with id {} not found", saDto.getId());
                        throw new EntityNotFoundException("Tariff with id " + saDto.getId() + " not found");
                    });
                    Double rate = savingsAccountRateRepository.findLatestRateIdByTariffId(id).orElseGet(() -> {
                        log.info("Rate with tariff id {} not found", id);
                        throw new EntityNotFoundException("Rate with tariff id " + id + " not found");
                    });
                    saDto.setTariffId(id);
                    saDto.setRate(rate);
                });
        return savingsAccountDtos;
    }

    @Transactional
    @Async("calculatePercentsExecutor")
    public void calculatePercent() {
        Random random = new Random();
        double accountBalance = random.nextDouble(100.0, 1001.0);
        int currentYearDays = Year.now().length();
        double dailyRate = (double) 150 / currentYearDays;
        double dailyInterest = accountBalance * (dailyRate / 100);
        double newBalance = accountBalance + dailyInterest;
        System.out.println(Thread.currentThread().getName()+ "Current balance = "+accountBalance);
        System.out.println(Thread.currentThread().getName() + " = new balance " + newBalance);
//        BalanceDto balanceDto = balanceService.getBalanceByAccountId(5L);
//        balanceDto.getActualBalance();
    }
}
