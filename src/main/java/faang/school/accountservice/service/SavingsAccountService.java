package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.OpenSavingsAccountRequest;
import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.mapper.SavingAccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.model.SavingsAccountTariffHistory;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.service.account.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SavingsAccountService {
    private final AccountRepository accountRepository;
    private final SavingsAccountRepository savingsAccountRepository;
    private final TariffService tariffService;
    private final FreeAccountNumberService freeAccountNumberService;
    private final AccountMapper accountMapper;
    private final SavingAccountMapper savingAccountMapper;

    @Transactional
    public SavingsAccountDto openSavingsAccount(OpenSavingsAccountRequest request) {
        Account account = createAccount(request);
        account = accountRepository.save(account);
        SavingsAccount savingsAccount = createSavingsAccount(account, request.getInitialTariffId());
        return savingAccountMapper.toDto(savingsAccount);
    }

    private Account createAccount(OpenSavingsAccountRequest request) {
        AccountDto accountDto = AccountDto.builder()
                .ownerId(request.getOwnerId())
                .accountType(AccountType.DEPOSIT)
                .currency(request.getCurrency())
                .build();
        BigInteger accountNumber = freeAccountNumberService.getFreeNumber(AccountType.DEPOSIT);
        accountDto.setNumber(accountNumber);
        return accountMapper.toEntity(accountDto);
    }

    private SavingsAccount createSavingsAccount(Account account, Long initialTariffId){
        SavingsAccount savingsAccount = SavingsAccount.builder()
                .account(account)
                .lastInterestCalculatedDate(LocalDateTime.now())
                .build();

        Tariff initialTariff = tariffService.getTariff(initialTariffId);
        SavingsAccountTariffHistory initialTariffHistory = SavingsAccountTariffHistory.builder()
                .tariff(initialTariff)
                .build();

        savingsAccount.getTariffHistory().add(initialTariffHistory);

        return savingsAccountRepository.save(savingsAccount);
    }

    public SavingsAccountDto getSavingsAccountById(Long id){
        SavingsAccount savingsAccount = getSavingsAccount(id);
        return savingAccountMapper.toDto(savingsAccount);
    }

    public SavingsAccountDto getSavingsAccountByOwnerId(Long ownerId){
        SavingsAccount savingsAccount = getSavingsAccountByOwner(ownerId);
        return savingAccountMapper.toDto(savingsAccount);
    }

    private SavingsAccount getSavingsAccount(Long id){
        return savingsAccountRepository.findByAccountId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings account not found"));
    }

    private SavingsAccount getSavingsAccountByOwner(Long ownerId){
        return savingsAccountRepository.findByAccountOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings account not found"));
    }
}
