package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.AccountStatus;
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

    private Long balanceId;

    @BeforeEach
    void setUp() {
        accountNumber = "ACC0123456789";
        balanceId = 1L;
        account = Account.builder()
                .id(1L)
                .accountNumber(accountNumber)
                .status(AccountStatus.ACTIVE)
                .ownerId(1L)
                .build();

        balance = Balance.builder()
                .id(1L)
                .account(account)
                .authorizedBalance(new BigDecimal("0.00"))
                .actualBalance(new BigDecimal("0.00"))
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Authorize balance: success")
    void testAuthorize_Success() {
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
        account.setBalance(balance);

        when(accountService.findByAccountNumber(accountNumber)).thenReturn(account);

        DataValidationException ex = assertThrows(DataValidationException.class, () -> balanceService.authorize(accountNumber));
        assertEquals("Balance already authorized on this account", ex.getMessage());
    }

    @Test
    @DisplayName("Deposit authorized balance: success")
    void testDepositAuthorized_Success() {
        when(balanceRepository.getReferenceById(balanceId)).thenReturn(balance);

        BalanceDto result = balanceService.depositAuthorized(balanceId, new BigDecimal("100.00"));

        verify(balanceRepository, times(1)).getReferenceById(balanceId);
        assertEquals(new BigDecimal("100.00"), result.authorizedBalance());
    }

    @Test
    @DisplayName("Deposit actual balance: success")
    void testDepositActual_Success() {
        when(balanceRepository.getReferenceById(balanceId)).thenReturn(balance);

        BalanceDto result = balanceService.depositActual(balanceId, new BigDecimal("100.00"));

        verify(balanceRepository, times(1)).getReferenceById(balanceId);
        assertEquals(new BigDecimal("100.00"), result.actualBalance());
    }

    @Test
    void testWithdrawAuthorized_Success() {
        balance.setAuthorizedBalance(new BigDecimal("100.00"));
        when(balanceRepository.getReferenceById(balanceId)).thenReturn(balance);

        BalanceDto result = balanceService.withdrawAuthorized(balanceId, new BigDecimal("99.01"));

        verify(balanceRepository, times(1)).getReferenceById(balanceId);
        assertEquals(new BigDecimal("0.99"), result.authorizedBalance());
    }

    @Test
    void testWithdrawAuthorized_InsufficientFunds() {
        BigDecimal amount = new BigDecimal("0.01");
        balance.setAuthorizedBalance(new BigDecimal("0.00"));
        when(balanceRepository.getReferenceById(balanceId)).thenReturn(balance);

        InsufficientBalanceException ex = assertThrows(InsufficientBalanceException.class, () -> balanceService.withdrawAuthorized(balanceId, amount));
        assertEquals("Balance is not enough for withdrawal", ex.getMessage());
    }

    @Test
    void testWithdrawActual_Success() {
        balance.setActualBalance(new BigDecimal("100.00"));
        when(balanceRepository.getReferenceById(balanceId)).thenReturn(balance);

        BalanceDto result = balanceService.withdrawActual(balanceId, new BigDecimal("99.01"));

        verify(balanceRepository, times(1)).getReferenceById(balanceId);
        assertEquals(new BigDecimal("0.99"), result.actualBalance());
    }

    @Test
    void testWithdrawActual_InsufficientFunds() {
        BigDecimal amount = new BigDecimal("0.01");
        balance.setAuthorizedBalance(new BigDecimal("0.00"));
        when(balanceRepository.getReferenceById(balanceId)).thenReturn(balance);

        InsufficientBalanceException ex = assertThrows(InsufficientBalanceException.class, () -> balanceService.withdrawActual(balanceId, amount));
        assertEquals("Balance is not enough for withdrawal", ex.getMessage());
    }
}
