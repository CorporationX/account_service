package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.OpenSavingsAccountRequest;
import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.mapper.SavingAccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.model.SavingsAccountTariffHistory;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.owner.Owner;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.service.account.FreeAccountNumberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingsAccountServiceTest {

    @InjectMocks
    private SavingsAccountService savingsAccountService;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private SavingsAccountRepository savingsAccountRepository;
    @Mock
    private TariffService tariffService;
    @Mock
    private FreeAccountNumberService freeAccountNumberService;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private SavingAccountMapper savingAccountMapper;

    @Test
    void testOpenSavingsAccount() {
        Long ownerId = 1L;
        Currency currency = Currency.USD;
        Long initialTariffId = 2L;
        OpenSavingsAccountRequest request = OpenSavingsAccountRequest.builder()
                .ownerId(ownerId)
                .currency(currency)
                .initialTariffId(initialTariffId)
                .build();

        Account account = Account.builder()
                .owner(new Owner())
                .accountType(AccountType.DEPOSIT)
                .currency(currency)
                .build();

        SavingsAccount savedSavingsAccount = SavingsAccount.builder()
                .account(account)
                .lastInterestCalculatedDate(LocalDateTime.now())
                .tariffHistory(Collections.singletonList(SavingsAccountTariffHistory.builder().tariff(new Tariff()).build()))
                .build();

        when(accountMapper.toEntity(any(AccountDto.class))).thenReturn(account);
        when(freeAccountNumberService.getFreeNumber(AccountType.DEPOSIT)).thenReturn(BigInteger.ONE);
        when(tariffService.getTariff(initialTariffId)).thenReturn(new Tariff());
        when(accountRepository.save(account)).thenReturn(account);
        when(savingsAccountRepository.save(any(SavingsAccount.class))).thenReturn(savedSavingsAccount);
        when(savingAccountMapper.toDto(savedSavingsAccount)).thenReturn(new SavingsAccountDto());

        SavingsAccountDto result = savingsAccountService.openSavingsAccount(request);

        assertEquals(new SavingsAccountDto(), result);
        verify(accountRepository, times(1)).save(account);
        verify(savingsAccountRepository, times(1)).save(savedSavingsAccount);
    }


    @Test
    void shouldGetSavingsAccountById() {
        Long accountId = 1L;
        SavingsAccount savingsAccount = new SavingsAccount();
        SavingsAccountDto expectedSavingsAccountDto = new SavingsAccountDto();
        when(savingsAccountRepository.findByAccountId(accountId)).thenReturn(Optional.of(savingsAccount));
        when(savingAccountMapper.toDto(savingsAccount)).thenReturn(expectedSavingsAccountDto);
        SavingsAccountDto result = savingsAccountService.getSavingsAccountById(accountId);
        assertEquals(expectedSavingsAccountDto, result);
    }

    @Test
    void shouldGetSavingsAccountByOwnerId() {
        Long ownerId = 1L;
        SavingsAccount savingsAccount = new SavingsAccount();
        SavingsAccountDto expectedSavingsAccountDto = new SavingsAccountDto();
        when(savingsAccountRepository.findByAccountOwnerId(ownerId)).thenReturn(Optional.of(savingsAccount));
        when(savingAccountMapper.toDto(savingsAccount)).thenReturn(expectedSavingsAccountDto);
        SavingsAccountDto result = savingsAccountService.getSavingsAccountByOwnerId(ownerId);
        assertEquals(expectedSavingsAccountDto, result);
    }
}