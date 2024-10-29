package faang.school.accountservice.service.impl;

import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.model.dto.SavingsAccountDto;
import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.entity.SavingsAccount;
import faang.school.accountservice.model.entity.SavingsAccountRate;
import faang.school.accountservice.model.entity.Tariff;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRateRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.service.SavingsAccountService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavingsAccountServiceImpl implements SavingsAccountService {
    private final SavingsAccountMapper savingsAccountMapper;
    private final AccountRepository accountRepository;
    private final SavingsAccountRepository savingsAccountRepository;
    private final TariffRepository tariffRepository;
    private final SavingsAccountRateRepository savingsAccountRateRepository;

    @Transactional
    @Override
    public SavingsAccountDto openSavingsAccount(SavingsAccountDto savingsAccountDto) {
        Tariff tariff = tariffRepository.findById(savingsAccountDto.getTariffId()).orElseGet(() -> {
            log.info("Tariff with id {} not found", savingsAccountDto.getTariffId());
            throw new EntityNotFoundException("Tariff with id " + savingsAccountDto.getTariffId() + " not found");
        });

        SavingsAccountRate savingsAccountRate = tariff.getSavingsAccountRate();
        if (savingsAccountRate == null) {
            log.info("SavingsAccountRate with id {} not found", tariff.getSavingsAccountRate());
            throw new EntityNotFoundException("SavingsAccountRate " + tariff.getSavingsAccountRate() + " not found");
        }

        Account account = accountRepository.findById(savingsAccountDto.getAccountId()).orElseGet(() -> {
            log.info("Account with id {} not found", savingsAccountDto.getAccountId());
            throw new EntityNotFoundException("Account with id " + savingsAccountDto.getAccountId() + " not found");
        });
//TODO

        SavingsAccount savingsAccount = SavingsAccount.builder()
                .account(account)
                .accountNumber(account.getNumber())
//                .tariff(tariff)
//                .savingsAccountRate(savingsAccountRate)
                .build();

        savingsAccount = savingsAccountRepository.save(savingsAccount);

        SavingsAccountDto savingsAccountResult = savingsAccountMapper
                .savingsAccountToSavingsAccountDto(savingsAccount);
//        savingsAccountResult.setSavingsAccountRate(savingsAccount.getSavingsAccountRate().getRate());

        return savingsAccountResult;
    }

    @Override
    public SavingsAccountDto getSavingsAccount(Long id) {
        SavingsAccount savingsAccount = savingsAccountRepository.findById(id).orElseThrow();
        return savingsAccountMapper.savingsAccountToSavingsAccountDto(savingsAccount);
    }
}
