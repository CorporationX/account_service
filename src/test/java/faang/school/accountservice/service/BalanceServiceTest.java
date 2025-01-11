package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.BalanceStatus;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.exception.InsufficientBalanceException;
import faang.school.accountservice.mapper.BalanceMapperImpl;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private BalanceRepository balanceRepository;

    @Spy
    private BalanceMapperImpl balanceMapper;

    @InjectMocks
    private BalanceService balanceService;

    private String accountNumber;
    private Account account;
    private Balance balance;

    @BeforeEach
    void setUp() {
        accountNumber = "13123120123456789";
        balance = Balance.builder()
                .id(1L)
                .account(account)
                .authorizedBalance(new BigDecimal("0.00"))
                .actualBalance(new BigDecimal("0.00"))
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        account = Account.builder()
                .id(1L)
                .accountNumber(accountNumber)
                .balance(balance)
                .status(AccountStatus.ACTIVE)
                .ownerId(1L)
                .balanceStatus(BalanceStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Authorize balance: success")
    void testAuthorize_Success() {
        account.setBalanceStatus(BalanceStatus.NEW);
        account.setBalance(null);
        when(accountService.findByAccountNumber(accountNumber)).thenReturn(account);
        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);

        BalanceDto result = balanceService.authorize(accountNumber);

        verify(accountService, times(1)).findByAccountNumber(accountNumber);
        verify(balanceRepository, times(1)).save(any(Balance.class));
        assertNotNull(result);
    }

    @Test
    @DisplayName("Authorize balance: balance already exists")
    void testAuthorize_BalanceAlreadyExists() {
        account.setBalanceStatus(BalanceStatus.ACTIVE);

        when(accountService.findByAccountNumber(accountNumber)).thenReturn(account);

        DataValidationException ex = assertThrows(DataValidationException.class, () -> balanceService.authorize(accountNumber));
        assertEquals("Balance already authorized on this account", ex.getMessage());
    }

    @Test
    @DisplayName("Deposit authorized balance: success")
    void testDepositAuthorized_Success() {
        when(accountService.findByAccountNumber(accountNumber)).thenReturn(account);

        BalanceDto result = balanceService.depositAuthorized(accountNumber, new BigDecimal("100.00"));

        verify(accountService, times(1)).findByAccountNumber(accountNumber);
        assertEquals(new BigDecimal("100.00"), result.authorizedBalance());
    }

    @Test
    @DisplayName("Deposit authorized balance: balance not authorized")
    void testDepositAuthorized_BalanceNotAuthorized() {
        BigDecimal amount = new BigDecimal("100.00");
        account.setBalanceStatus(BalanceStatus.NEW);
        when(accountService.findByAccountNumber(accountNumber)).thenReturn(account);

        DataValidationException ex = assertThrows(DataValidationException.class, () -> balanceService.depositAuthorized(accountNumber, amount));
        assertEquals("Balance not authorized on this account", ex.getMessage());
    }

    @Test
    @DisplayName("Deposit actual balance: success")
    void testDepositActual_Success() {
        when(accountService.findByAccountNumber(accountNumber)).thenReturn(account);

        BalanceDto result = balanceService.depositActual(accountNumber, new BigDecimal("100.00"));

        verify(accountService, times(1)).findByAccountNumber(accountNumber);
        assertEquals(new BigDecimal("100.00"), result.actualBalance());
    }

    @Test
    @DisplayName("Deposit actual balance: balance not authorized")
    void testDepositActual_BalanceNotAuthorized() {
        BigDecimal amount = new BigDecimal("100.00");
        account.setBalanceStatus(BalanceStatus.NEW);
        when(accountService.findByAccountNumber(accountNumber)).thenReturn(account);

        DataValidationException ex = assertThrows(DataValidationException.class, () -> balanceService.depositActual(accountNumber, amount));
        assertEquals("Balance not authorized on this account", ex.getMessage());
    }

    @Test
    @DisplayName("Withdraw authorized balance: success")
    void testWithdrawAuthorized_Success() {
        balance.setAuthorizedBalance(new BigDecimal("100.00"));
        when(accountService.findByAccountNumber(accountNumber)).thenReturn(account);

        BalanceDto result = balanceService.withdrawAuthorized(accountNumber, new BigDecimal("99.01"));

        verify(accountService, times(1)).findByAccountNumber(accountNumber);
        assertEquals(new BigDecimal("0.99"), result.authorizedBalance());
    }

    @Test
    @DisplayName("Withdraw authorized balance: balance not authorized")
    void testWithdrawAuthorized_BalanceNotAuthorized() {
        BigDecimal amount = new BigDecimal("100.00");
        account.setBalanceStatus(BalanceStatus.NEW);
        when(accountService.findByAccountNumber(accountNumber)).thenReturn(account);

        DataValidationException ex = assertThrows(DataValidationException.class, () -> balanceService.withdrawAuthorized(accountNumber, amount));
        assertEquals("Balance not authorized on this account", ex.getMessage());
    }

    @Test
    @DisplayName("Withdraw authorized balance: insufficient funds")
    void testWithdrawAuthorized_InsufficientFunds() {
        BigDecimal amount = new BigDecimal("0.01");
        balance.setAuthorizedBalance(new BigDecimal("0.00"));
        when(accountService.findByAccountNumber(accountNumber)).thenReturn(account);

        InsufficientBalanceException ex = assertThrows(InsufficientBalanceException.class, () -> balanceService.withdrawAuthorized(accountNumber, amount));
        assertEquals("Balance is not enough for withdrawal", ex.getMessage());
    }

    @Test
    @DisplayName("Withdraw actual balance: success")
    void testWithdrawActual_Success() {
        balance.setActualBalance(new BigDecimal("100.00"));
        when(accountService.findByAccountNumber(accountNumber)).thenReturn(account);

        BalanceDto result = balanceService.withdrawActual(accountNumber, new BigDecimal("99.01"));

        verify(accountService, times(1)).findByAccountNumber(accountNumber);
        assertEquals(new BigDecimal("0.99"), result.actualBalance());
    }

    @Test
    @DisplayName("Withdraw actual balance: balance not authorized")
    void testWithdrawActual_BalanceNotAuthorized() {
        BigDecimal amount = new BigDecimal("100.00");
        account.setBalanceStatus(BalanceStatus.NEW);
        when(accountService.findByAccountNumber(accountNumber)).thenReturn(account);

        DataValidationException ex = assertThrows(DataValidationException.class, () -> balanceService.withdrawActual(accountNumber, amount));
        assertEquals("Balance not authorized on this account", ex.getMessage());
    }

    @Test
    @DisplayName("Withdraw actual balance: insufficient funds")
    void testWithdrawActual_InsufficientFunds() {
        BigDecimal amount = new BigDecimal("0.01");
        balance.setAuthorizedBalance(new BigDecimal("0.00"));
        when(accountService.findByAccountNumber(accountNumber)).thenReturn(account);

        InsufficientBalanceException ex = assertThrows(InsufficientBalanceException.class, () -> balanceService.withdrawActual(accountNumber, amount));
        assertEquals("Balance is not enough for withdrawal", ex.getMessage());
    }
}
