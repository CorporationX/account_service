package faang.school.accountservice.service;

import static org.junit.jupiter.api.Assertions.*;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.entity.TariffHistory;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffHistoryRepository;
import faang.school.accountservice.repository.TariffRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavingsAccountServiceTest {
    @Mock
    private SavingsAccountMapper savingsAccountMapper;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Mock
    private TariffRepository tariffRepository;

    @Mock
    private TariffHistoryRepository tariffHistoryRepository;

    @InjectMocks
    private SavingsAccountService savingsAccountService;

    @Test
    void openSavingsAccount_WhenTariffNotFound_ShouldThrowException() {

        SavingsAccountDto dto = new SavingsAccountDto();
        dto.setTariffId(1L);
        dto.setAccountId(1L);

        when(tariffRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            savingsAccountService.openSavingsAccount(dto);
        });

        verify(tariffRepository).findById(1L);
        verifyNoMoreInteractions(accountRepository, savingsAccountRepository, tariffHistoryRepository);
    }

    @Test
    void openSavingsAccount_WhenAccountNotFound_ShouldThrowException() {

        SavingsAccountDto dto = new SavingsAccountDto();
        dto.setTariffId(1L);
        dto.setAccountId(1L);

        Tariff tariff = new Tariff();
        tariff.setId(1L);

        when(tariffRepository.findById(1L)).thenReturn(Optional.of(tariff));
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            savingsAccountService.openSavingsAccount(dto);
        });

        verify(tariffRepository).findById(1L);
        verify(accountRepository).findById(1L);
        verifyNoMoreInteractions(savingsAccountRepository, tariffHistoryRepository);
    }

    @Test
    void openSavingsAccount_WhenValidData_ShouldReturnDto() {

        SavingsAccountDto dto = new SavingsAccountDto();
        dto.setTariffId(1L);
        dto.setAccountId(1L);

        Tariff tariff = new Tariff();
        tariff.setId(1L);

        Account account = new Account();
        account.setId(1L);

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setId(1L);
        savingsAccount.setAccount(account);

        SavingsAccountDto expectedDto = new SavingsAccountDto();
        expectedDto.setId(1L);
        expectedDto.setTariffId(1L);

        when(tariffRepository.findById(1L)).thenReturn(Optional.of(tariff));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(savingsAccountRepository.save(any(SavingsAccount.class))).thenReturn(savingsAccount);
        when(savingsAccountMapper.toSavingsAccountDto(savingsAccount)).thenReturn(expectedDto);

        SavingsAccountDto result = savingsAccountService.openSavingsAccount(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getTariffId());

        verify(tariffRepository).findById(1L);
        verify(accountRepository).findById(1L);
        verify(savingsAccountRepository).save(any(SavingsAccount.class));
        verify(tariffHistoryRepository).save(any(TariffHistory.class));
        verify(savingsAccountMapper).toSavingsAccountDto(savingsAccount);
    }

    @Test
    void getSavingsAccount_WhenNotFound_ShouldThrowException() {

        when(savingsAccountRepository.findSavingsAccountWithDetails(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            savingsAccountService.getSavingsAccount(1L);
        });

        verify(savingsAccountRepository).findSavingsAccountWithDetails(1L);
    }

    @Test
    void getSavingsAccount_WhenFound_ShouldReturnDto() {

        SavingsAccountDto expectedDto = new SavingsAccountDto();
        expectedDto.setId(1L);

        when(savingsAccountRepository.findSavingsAccountWithDetails(1L)).thenReturn(Optional.of(expectedDto));

        SavingsAccountDto result = savingsAccountService.getSavingsAccount(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(savingsAccountRepository).findSavingsAccountWithDetails(1L);
    }

    @Test
    void getSavingsAccountByUserId_WhenNoAccounts_ShouldThrowException() {

        when(accountRepository.findNumbersByOwnerId(1L)).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> {
            savingsAccountService.getSavingsAccountByUserId(1L);
        });

        verify(accountRepository).findNumbersByOwnerId(1L);
        verifyNoInteractions(savingsAccountRepository);
    }

    @Test
    void getSavingsAccountByUserId_WhenNoSavingsAccounts_ShouldThrowException() {

        when(accountRepository.findNumbersByOwnerId(1L)).thenReturn(List.of("12345"));
        when(savingsAccountRepository.getSavingsAccountsWithLastTariffRate(List.of("12345"))).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> {
            savingsAccountService.getSavingsAccountByUserId(1L);
        });

        verify(accountRepository).findNumbersByOwnerId(1L);
        verify(savingsAccountRepository).getSavingsAccountsWithLastTariffRate(List.of("12345"));
    }
}