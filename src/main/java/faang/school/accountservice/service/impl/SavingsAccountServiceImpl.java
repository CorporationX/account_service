package faang.school.accountservice.service.impl;

import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.model.dto.SavingsAccountDto;
import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.entity.SavingsAccount;
import faang.school.accountservice.model.entity.Tariff;
import faang.school.accountservice.model.entity.TariffHistory;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffHistoryRepository;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.service.SavingsAccountService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavingsAccountServiceImpl implements SavingsAccountService {
    private final SavingsAccountMapper savingsAccountMapper;
    private final AccountRepository accountRepository;
    private final SavingsAccountRepository savingsAccountRepository;
    private final TariffRepository tariffRepository;
    private final TariffHistoryRepository tariffHistoryRepository;

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
                .tariffId(tariff)
                .build();
        tariffHistoryRepository.save(tariffHistory);

        return savingsAccountMapper
                .savingsAccountToSavingsAccountDto(savingsAccount);
    }

    @Override
    public SavingsAccountDto getSavingsAccount(Long id) {
        SavingsAccount savingsAccount = savingsAccountRepository.findById(id).orElseGet(() -> {
            log.info("SavingsAccount with id {} not found", id);
            throw new EntityNotFoundException("SavingsAccount with id " + id + " not found");
        });

        Long tariffId = tariffHistoryRepository.findLatestTariffIdBySavingsAccountId(id).orElseGet(() -> {
            log.info("Tariff with id {} not found", id);
            throw new EntityNotFoundException("Tariff with id " + id + " not found");
        });

        SavingsAccountDto savingsAccountDto = savingsAccountMapper.savingsAccountToSavingsAccountDto(savingsAccount);
        savingsAccountDto.setTariffId(tariffId);
        return savingsAccountDto;
    }

    @Override
    public List<SavingsAccountDto> getSavingsAccountByUserId(Long userId) {
        List<String> numbers = accountRepository.findNumbersByUserId(userId);
        if (numbers.isEmpty()) {
            throw new EntityNotFoundException("Accounts with user id " + userId + " not found");
        }

        List<SavingsAccount> savingsAccounts = savingsAccountRepository.findSaIdsByAccountNumbers(numbers);
        List<SavingsAccountDto> savingsAccountDtos = savingsAccountMapper.toDtos(savingsAccounts);
        savingsAccountDtos
                .forEach(saDto -> {
                    Long id = tariffHistoryRepository.findLatestTariffIdBySavingsAccountId(saDto.getId()).orElseGet(()->{
                        log.info("Tariff with id {} not found", saDto.getId());
                        throw new EntityNotFoundException("Tariff with id " + saDto.getId() + " not found");
                    });
                    saDto.setTariffId(id);
                });

        return savingsAccountDtos;
    }

}
