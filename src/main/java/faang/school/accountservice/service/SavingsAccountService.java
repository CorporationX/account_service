package faang.school.accountservice.service;

import faang.school.accountservice.dto.savings.SavingsAccountDto;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.savings.SavingsAccount;
import faang.school.accountservice.model.savings.Tariff;
import faang.school.accountservice.model.savings.TariffHistory;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final TariffHistoryRepository tariffHistoryRepository;
    private final AccountRepository accountRepository;
    private final TariffService tariffService;
    private final SavingsAccountMapper savingsAccountMapper;

    @Transactional
    public SavingsAccount openSavingsAccount(UUID accountId, UUID tariffId) {
        return findOrCreateSavingsAccount(accountId, tariffId);
    }

    private SavingsAccount findOrCreateSavingsAccount(UUID accountId, UUID tariffId) {
        return savingsAccountRepository.findById(accountId).orElseGet(() -> {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            Tariff tariff = tariffService.getTariffById(tariffId);

            SavingsAccount savingsAccount = initializeSavingsAccount(accountId, account);

            addTariffHistory(savingsAccount, tariff);

            return savingsAccount;
        });
    }

    private SavingsAccount initializeSavingsAccount(UUID id, Account account) {
        SavingsAccount savingsAccount = SavingsAccount.builder()
                .id(id)
                .account(account)
                .tariffHistory(new ArrayList<>())
                .lastCalculatedAt(null)
                .build();
        return savingsAccountRepository.save(savingsAccount);
    }

    private void addTariffHistory(SavingsAccount savingsAccount, Tariff tariff) {
        TariffHistory tariffHistory = TariffHistory.builder()
                .tariff(tariff)
                .savingsAccount(savingsAccount)
                .appliedAt(LocalDateTime.now())
                .build();

        tariffHistoryRepository.save(tariffHistory);
        savingsAccount.getTariffHistory().add(tariffHistory);
    }


    @Transactional(readOnly = true)
    public SavingsAccountDto getSavingsAccountById(UUID savingsAccountId) {
        SavingsAccount savingsAccount = savingsAccountRepository.findById(savingsAccountId)
                .orElseThrow(() -> new RuntimeException("Savings account not found"));
        return savingsAccountMapper.toSavingsAccountDto(savingsAccount);
    }

    @Transactional(readOnly = true)
    public SavingsAccountDto getSavingsAccountByOwnerId(UUID ownerId) {
        Account account = accountRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("Account for owner not found"));
        SavingsAccount savingsAccount = savingsAccountRepository.findById(account.getId())
                .orElseThrow(() -> new RuntimeException("Savings account not found"));
        return savingsAccountMapper.toSavingsAccountDto(savingsAccount);
    }
}

