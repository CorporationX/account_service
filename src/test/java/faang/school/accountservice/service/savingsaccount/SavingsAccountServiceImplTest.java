package faang.school.accountservice.service.savingsaccount;

import faang.school.accountservice.dto.savingsaccount.DepositDto;
import faang.school.accountservice.dto.savingsaccount.OpenSavingsAccountDto;
import faang.school.accountservice.dto.savingsaccount.SavingsAccountResponseDto;
import faang.school.accountservice.dto.savingsaccount.WithdrawDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.InsufficientFundsException;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.service.tariff.TariffService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavingsAccountServiceImplTest {

    @Mock
    private SavingsAccountRepository savingsAccountRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TariffService tariffService;
    @Mock
    private SavingsAccountMapper savingsAccountMapper;

    @InjectMocks
    private SavingsAccountServiceImpl savingsAccountService;

    @Test
    void openSavingsAccount_ShouldSuccessfullyCreateAccount() {
        // Arrange
        OpenSavingsAccountDto dto = new OpenSavingsAccountDto();
        dto.setAccountId("acc123");
        dto.setInitialTariffId(1L);

        Account account = new Account();
        account.setAccountNumber("acc123");

        SavingsAccount savedAccount = new SavingsAccount();
        savedAccount.setAccount(account);
        savedAccount.setTariffHistory(new ArrayList<>(List.of(1L)));

        SavingsAccountResponseDto expectedResponse = new SavingsAccountResponseDto();
        expectedResponse.setAccountId("acc123");

        when(accountRepository.findById("acc123")).thenReturn(Optional.of(account));
        when(savingsAccountRepository.existsByAccount(account)).thenReturn(false);
        when(tariffService.existsTariffById(1L)).thenReturn(true);
        when(savingsAccountRepository.save(any(SavingsAccount.class))).thenReturn(savedAccount);
        when(savingsAccountMapper.toSavingsAccountResponseDto(savedAccount)).thenReturn(expectedResponse);

        // Act
        SavingsAccountResponseDto result = savingsAccountService.openSavingsAccount(dto);

        // Assert
        assertNotNull(result);
        assertEquals("acc123", result.getAccountId());
        verify(savingsAccountRepository).save(any(SavingsAccount.class));
    }

    @Test
    void openSavingsAccount_ShouldThrowWhenAccountNotFound() {
        // Arrange
        OpenSavingsAccountDto dto = new OpenSavingsAccountDto();
        dto.setAccountId("non-existent");
        dto.setInitialTariffId(1L);

        when(accountRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class,
                () -> savingsAccountService.openSavingsAccount(dto));
    }

    @Test
    void openSavingsAccount_ShouldThrowWhenAccountAlreadyExists() {
        // Arrange
        OpenSavingsAccountDto dto = new OpenSavingsAccountDto();
        dto.setAccountId("acc123");
        dto.setInitialTariffId(1L);

        Account account = new Account();
        account.setAccountNumber("acc123");

        when(accountRepository.findById("acc123")).thenReturn(Optional.of(account));
        when(savingsAccountRepository.existsByAccount(account)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> savingsAccountService.openSavingsAccount(dto));
    }

    @Test
    void getSavingsAccountById_ShouldReturnAccount() {
        // Arrange
        String accountId = "acc123";
        SavingsAccount savingsAccount = new SavingsAccount();
        Account account = new Account();
        account.setAccountNumber(accountId);
        savingsAccount.setAccount(account);

        SavingsAccountResponseDto expectedResponse = new SavingsAccountResponseDto();
        expectedResponse.setAccountId(accountId);

        when(savingsAccountRepository.findByAccountId(accountId)).thenReturn(Optional.of(savingsAccount));
        when(savingsAccountMapper.toSavingsAccountResponseDto(savingsAccount)).thenReturn(expectedResponse);

        // Act
        SavingsAccountResponseDto result = savingsAccountService.getSavingsAccountById(accountId);

        // Assert
        assertNotNull(result);
        assertEquals(accountId, result.getAccountId());
    }

    @Test
    void changeTariff_ShouldSuccessfullyUpdateTariff() {
        // Arrange
        Long savingsAccountId = 1L;
        Long newTariffId = 2L;

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setId(savingsAccountId);
        savingsAccount.setTariffHistory(new ArrayList<>(List.of(1L)));

        SavingsAccountResponseDto expectedResponse = new SavingsAccountResponseDto();
        expectedResponse.setId(savingsAccountId);

        // Настройка моков
        when(savingsAccountRepository.findById(savingsAccountId)).thenReturn(Optional.of(savingsAccount));
        when(tariffService.existsTariffById(newTariffId)).thenReturn(false); // Тариф существует
        when(savingsAccountRepository.save(savingsAccount)).thenReturn(savingsAccount);
        when(savingsAccountMapper.toSavingsAccountResponseDto(savingsAccount)).thenReturn(expectedResponse);

        // Act
        SavingsAccountResponseDto result = savingsAccountService.changeTariff(savingsAccountId, newTariffId);

        // Assert
        assertNotNull(result);
        assertEquals(savingsAccountId, result.getId());
        assertEquals(2, savingsAccount.getTariffHistory().size());
        assertEquals(newTariffId, savingsAccount.getTariffHistory().get(1));
        verify(savingsAccountRepository).save(savingsAccount);

        // Проверка, что метод existsTariffById был вызван с правильным аргументом
        verify(tariffService).existsTariffById(newTariffId);
    }

    @Test
    void deposit_ShouldIncreaseBalance() {
        // Arrange
        Long savingsAccountId = 1L;
        BigDecimal amount = new BigDecimal("100.00");
        DepositDto depositDto = new DepositDto();
        depositDto.setSavingsAccountId(savingsAccountId);
        depositDto.setAmount(amount);

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setId(savingsAccountId);
        savingsAccount.setBalance(BigDecimal.ZERO);

        when(savingsAccountRepository.findById(savingsAccountId)).thenReturn(Optional.of(savingsAccount));

        // Act
        savingsAccountService.deposit(depositDto);

        // Assert
        assertEquals(amount, savingsAccount.getBalance());
    }

    @Test
    void withdraw_ShouldDecreaseBalanceWhenSufficientFunds() {
        // Arrange
        Long savingsAccountId = 1L;
        BigDecimal initialBalance = new BigDecimal("200.00");
        BigDecimal withdrawAmount = new BigDecimal("100.00");
        WithdrawDto withdrawDto = new WithdrawDto();
        withdrawDto.setSavingsAccountId(savingsAccountId);
        withdrawDto.setAmount(withdrawAmount);

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setId(savingsAccountId);
        savingsAccount.setBalance(initialBalance);

        when(savingsAccountRepository.findById(savingsAccountId)).thenReturn(Optional.of(savingsAccount));

        // Act
        savingsAccountService.withdraw(withdrawDto);

        // Assert
        assertEquals(new BigDecimal("100.00"), savingsAccount.getBalance());
    }

    @Test
    void withdraw_ShouldThrowWhenInsufficientFunds() {
        // Arrange
        Long savingsAccountId = 1L;
        BigDecimal initialBalance = new BigDecimal("50.00");
        BigDecimal withdrawAmount = new BigDecimal("100.00");
        WithdrawDto withdrawDto = new WithdrawDto();
        withdrawDto.setSavingsAccountId(savingsAccountId);
        withdrawDto.setAmount(withdrawAmount);

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setId(savingsAccountId);
        savingsAccount.setBalance(initialBalance);

        when(savingsAccountRepository.findById(savingsAccountId)).thenReturn(Optional.of(savingsAccount));

        // Act & Assert
        assertThrows(InsufficientFundsException.class,
                () -> savingsAccountService.withdraw(withdrawDto));
        assertEquals(initialBalance, savingsAccount.getBalance());
    }
}