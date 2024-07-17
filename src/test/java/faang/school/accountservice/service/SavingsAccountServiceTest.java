package faang.school.accountservice.service;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.TariffHistory;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SavingsAccountServiceTest {
    @InjectMocks
    private SavingsAccountService savingsAccountService;
    @Mock
    private SavingsAccountRepository savingsAccountRepository;
    @Mock
    private SavingsAccountMapper savingsAccountMapper;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TariffRepository tariffRepository;

    private Tariff tariff;
    private Account account;
    private SavingsAccount savingsAccount;
    private SavingsAccountDto savingsAccountDto;
    private TariffHistory tariffHistory;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);

        tariff = new Tariff();
        tariff.setId(1L);

        savingsAccount = new SavingsAccount();
        savingsAccount.setId(1L);
        savingsAccount.setAccount(account);
        savingsAccount.setVersion(0);
        savingsAccount.setLastInterestDate(LocalDate.from(LocalDateTime.now()));
        savingsAccount.setCreatedAt(LocalDateTime.now());
        savingsAccount.setUpdatedAt(LocalDateTime.now());

        tariffHistory = new TariffHistory();
        tariffHistory.setSavingsAccount(savingsAccount);
        tariffHistory.setTariffId(1L);
        savingsAccount.setTariffHistory(List.of(tariffHistory));

        savingsAccountDto = new SavingsAccountDto();
        savingsAccountDto.setId(1L);
    }

    @Test
    @DisplayName("Test create savings account.")
    public void testCreateSavingsAccount() {
        when(accountRepository.findById(anyLong())).thenReturn(account);
        when(tariffRepository.findById(anyLong())).thenReturn(Optional.of(tariff));
        when(savingsAccountMapper.toDto(any(SavingsAccount.class))).thenReturn(savingsAccountDto);

        SavingsAccountDto result = savingsAccountService.createSavingAccount(1L, 1L);

        assertNotNull(result);
        assertEquals(savingsAccountDto, result);
        verify(accountRepository, times(1)).findById(1L);
        verify(tariffRepository, times(1)).findById(1L);
        verify(savingsAccountMapper, times(1)).toDto(any(SavingsAccount.class));
    }

    @Test
    @DisplayName("Test create savings account with exception.")
    public void testCreateAccountWithException() {
        when(accountRepository.findById(anyLong())).thenReturn(account);
        when(tariffRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class,
                        () -> savingsAccountService.createSavingAccount(1L, 1L));

        assertEquals("No tariff with this id 1", exception.getMessage());
        verify(accountRepository, times(1)).findById(1L);
        verify(tariffRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test get savings account by id.")
    public void testGetSavingsAccountById() {
        when(savingsAccountRepository.findById(anyLong())).thenReturn(Optional.of(savingsAccount));
        when(savingsAccountMapper.toDto(any(SavingsAccount.class))).thenReturn(savingsAccountDto);

        SavingsAccountDto result = savingsAccountService.getSavingsAccountById(1L);

        assertNotNull(result);
        assertEquals(savingsAccountDto, result);
        verify(savingsAccountRepository, times(1)).findById(anyLong());
        verify(savingsAccountMapper, times(1)).toDto(any(SavingsAccount.class));
    }

    @Test
    @DisplayName("Test get savings account by account id.")
    public void testGetSavingsAccountByAccountId() {
        when(savingsAccountRepository.findByAccountId(anyLong())).thenReturn(Optional.of(savingsAccount));
        when(savingsAccountMapper.toDto(any(SavingsAccount.class))).thenReturn(savingsAccountDto);

        SavingsAccountDto result = savingsAccountService.getSavingsAccountByAccountId(1L);

        assertNotNull(result);
        assertEquals(savingsAccountDto, result);
        verify(savingsAccountRepository, times(1)).findByAccountId(anyLong());
        verify(savingsAccountMapper, times(1)).toDto(any(SavingsAccount.class));
    }
}
