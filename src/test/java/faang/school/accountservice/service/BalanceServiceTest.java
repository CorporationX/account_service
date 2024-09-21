package faang.school.accountservice.service;

import faang.school.accountservice.balanceValidator.BalanceValidator;
import faang.school.accountservice.dto.BalanceAuditDto;
import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.exception.DataNotFoundException;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.mapper.BalanceMapperImpl;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.validator.BalanceServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Spy
    private BalanceMapperImpl balanceMapper;

    @Mock
    private BalanceRepository balanceRepository;

    @Spy
    private BalanceValidator balanceValidator;

    @Mock
    private BalanceAuditRepository balanceAuditRepository;

    @Mock
    private BalanceAuditMapper balanceAuditMapper;

    @Mock
    private BalanceServiceValidator balanceServiceValidator;

    @InjectMocks
    private BalanceService balanceService;

    private BalanceDto balanceDto;
    private Account account;
    private Balance balance;
    private BalanceAudit balanceAudit;
    private long idAudit;
    private BalanceAuditDto balanceAuditDto;

    @BeforeEach
    public void setup() {
        idAudit = 1L;

        balanceDto = BalanceDto.builder()
                .accountId(1L)
                .authorizationBalance(BigDecimal.ZERO)
                .currentBalance(BigDecimal.ZERO)
                .build();

        account = Account.builder()
                .id(1L)
                .build();

        balance = Balance.builder()
                .id(1L)
                .account(account)
                .authorizationBalance(BigDecimal.ZERO)
                .currentBalance(BigDecimal.ZERO)
                .build();

        balanceAudit = BalanceAudit.builder()
                .id(1L)
                .accountId(account.getId())
                .transactionId(1L)
                .versionBalance(1L)
                .authorizationBalance(BigDecimal.ZERO)
                .currentBalance(BigDecimal.ZERO)
                .build();

        balanceAuditDto = BalanceAuditDto.builder().build();
    }

    @Test
    @DisplayName("create balance - account found")
    public void testCreateBalance_AccountFound() {
        when(accountRepository.findById(balanceDto.getAccountId())).thenReturn(Optional.of(account));
        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);
        when(balanceMapper.toDto(balance)).thenReturn(balanceDto);

        BalanceDto result = balanceService.createBalance(1L);

        assertEquals(balanceDto, result);
    }

    @Test
    @DisplayName("create balance - account not found")
    public void testCreateBalance_AccountNotFound() {
        when(accountRepository.findById(balanceDto.getAccountId())).thenReturn(Optional.empty());
        assertThrows(DataNotFoundException.class, () -> balanceService.createBalance(1L));
    }

    @Test
    @DisplayName("create balance when it is already exist")
    public void testCreateBalance_BalanceAlreadyExists() {
        account.setBalance(balance);
        when(accountRepository.findById(balanceDto.getAccountId())).thenReturn(Optional.of(account));
        assertThrows(RuntimeException.class, () -> balanceService.createBalance(1L));
    }

    @Test
    @DisplayName("get balance - account not found")
    void testGetBalance_AccountNotFound() {
        // Arrange
        long accountId = 1L;
        when(balanceRepository.findByAccountId(accountId)).thenReturn(java.util.Optional.empty());

        // Act and Assert
        assertThrows(DataNotFoundException.class, () -> balanceService.getBalance(accountId));
    }

    @Test
    @DisplayName("get balance - account found")
    void testGetBalance_AccountFound() {
        long accountId = 1L;
        when(balanceRepository.findByAccountId(accountId)).thenReturn(java.util.Optional.of(balance));
        when(balanceMapper.toDto(balance)).thenReturn(balanceDto);

        BalanceDto result = balanceService.getBalance(accountId);

        assertEquals(balanceDto, result);
    }

    @Test
    @DisplayName("Update balance - replenishment")
    public void testUpdateBalance_Replenishment() {
        BigDecimal amount = BigDecimal.valueOf(100);
        when(balanceMapper.toDto(any(Balance.class))).thenReturn(new BalanceDto());

        balanceService.increaseBalance(balance, amount);

        verify(balanceValidator, times(1)).isBalanceValid(balance);
        verify(balanceValidator, times(1)).isAmountValidForReplenishment(amount);
        assertEquals(BigDecimal.valueOf(100), balance.getAuthorizationBalance());
        verify(balanceRepository, times(1)).save(balance);
    }

    @Test
    @DisplayName("Update balance - withdrawal")
    public void testUpdateBalance_Withdrawal() {
        BigDecimal amount = BigDecimal.valueOf(100);
        balance.setAuthorizationBalance(BigDecimal.valueOf(200));
        when(balanceMapper.toDto(any(Balance.class))).thenReturn(new BalanceDto());

        balanceService.increaseBalance(balance, amount);

        verify(balanceValidator, times(1)).isBalanceValid(balance);
        assertEquals(BigDecimal.valueOf(300), balance.getAuthorizationBalance());
        verify(balanceRepository, times(1)).save(balance);
    }

    @Test
    @DisplayName("Update balance - invalid balance")
    public void testUpdateBalance_InvalidBalance() {
        BigDecimal amount = BigDecimal.valueOf(100);
        doThrow(new IllegalArgumentException()).when(balanceValidator).isBalanceValid(balance);

        assertThrows(IllegalArgumentException.class, () -> balanceService.increaseBalance(balance, amount));
    }

    @Test
    @DisplayName("Update balance - invalid amount for replenishment")
    public void testUpdateBalance_InvalidAmountForReplenishment() {
        BigDecimal amount = BigDecimal.valueOf(100);
        doThrow(new IllegalArgumentException()).when(balanceValidator).isAmountValidForReplenishment(amount);

        assertThrows(IllegalArgumentException.class, () -> balanceService.increaseBalance(balance, amount));
    }

    @Test
    @DisplayName("Update balance - invalid balance for withdrawal")
    public void testUpdateBalance_InvalidBalanceForWithdrawal() {
        BigDecimal amount = BigDecimal.valueOf(100);
        doThrow(new IllegalArgumentException()).when(balanceValidator).isAmountValidForReplenishment(amount);

        assertThrows(IllegalArgumentException.class, () -> balanceService.increaseBalance(balance, amount));
    }

    @Test
    @DisplayName("reduce balance - success")
    public void testReduceBalance_ValidBalanceAndAmount() {
        Balance balance = new Balance();
        balance.setAuthorizationBalance(BigDecimal.valueOf(100));
        BigDecimal amount = BigDecimal.valueOf(50);
        when(balanceMapper.toDto(any(Balance.class))).thenReturn(new BalanceDto());

        balanceService.reduceBalance(balance, amount);

        assertEquals(BigDecimal.valueOf(50), balance.getAuthorizationBalance());
        verify(balanceRepository, times(1)).save(balance);
    }

    @Test
    @DisplayName("reduce balance - invalid balance")
    public void testReduceBalance_InvalidBalance() {
        Balance balance = new Balance();
        BigDecimal amount = BigDecimal.valueOf(50);
        doThrow(new IllegalArgumentException("Check your balance")).when(balanceValidator).isBalanceValid(balance);

        assertThrows(IllegalArgumentException.class, () -> balanceService.reduceBalance(balance, amount));
    }

    @Test
    @DisplayName("reduce balance - invalid amount")
    public void testReduceBalance_InvalidAmount() {
        Balance balance = new Balance();
        balance.setAuthorizationBalance(BigDecimal.valueOf(100));
        BigDecimal amount = BigDecimal.valueOf(150);
        doThrow(new IllegalArgumentException("Check your balance")).when(balanceValidator).isBalanceValidForWithdrawal(balance, amount);

        assertThrows(IllegalArgumentException.class, () -> balanceService.reduceBalance(balance, amount));
    }
}
