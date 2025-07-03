package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.common.DataValidationException;
import faang.school.accountservice.exception.common.PreConditionFailedException;
import faang.school.accountservice.exception.common.RecordNotFoundException;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {


    @Mock
    private AccountService accountService;
    @Mock
    private BalanceRepository balanceRepository;
    @InjectMocks
    private BalanceService balanceService;
    private UUID accountId;
    private UUID balanceId;
    private Account expectedAccount;
    private Balance expectedBalance;

    @BeforeEach
    public void beforeEach() {
        accountId = UUID.randomUUID();
        balanceId = UUID.randomUUID();

        expectedAccount = Account.builder()
                .id(accountId)
                .build();

        expectedBalance = Balance.builder()
                .id(balanceId)
                .account(expectedAccount)
                .build();
    }

    @Test
    void testGetBalanceByAccountIdReturnBalanceWhenExists() {
        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(expectedBalance));

        Balance actualBalance = balanceService.getBalanceByAccountId(accountId);

        assertEquals(balanceId, actualBalance.getId());
        assertEquals(accountId, actualBalance.getAccount().getId());
        verify(balanceRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void testGetBalanceByAccountThrowWhenNotFound() {
        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> balanceService.getBalanceByAccountId(accountId)
        );

        assertTrue(exception.getMessage().contains(accountId.toString()));
        verify(balanceRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void testGetBalanceByAccountNumberReturnBalanceWhenExists() {
        String accountNumber = "ACC1234567890";

        expectedAccount.setAccountNumber(accountNumber);

        when(balanceRepository.findByAccount_AccountNumber(accountNumber)).thenReturn(Optional.of(expectedBalance));

        Balance actual = balanceService.getBalanceByAccountNumber(accountNumber);

        assertEquals(balanceId, actual.getId());
        assertEquals(accountNumber, actual.getAccount().getAccountNumber());
        verify(balanceRepository, times(1)).findByAccount_AccountNumber(accountNumber);
    }

    @Test
    void testGetBalanceByAccountNumberThrowWhenNotFound() {
        String accountNumber = "ACC1234567890";
        when(balanceRepository.findByAccount_AccountNumber(accountNumber)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> balanceService.getBalanceByAccountNumber(accountNumber)
        );
        assertTrue(exception.getMessage().contains(accountNumber));
        verify(balanceRepository, times(1)).findByAccount_AccountNumber(accountNumber);
    }

    @Test
    void testCreateBalance() {
        when(accountService.getAccountById(accountId)).thenReturn(expectedAccount);
        when(balanceRepository.existsByAccountId(accountId)).thenReturn(false);

        when(balanceRepository.save(any(Balance.class))).thenReturn(expectedBalance);

        Balance result = balanceService.createBalance(accountId);

        assertEquals(accountId, result.getAccount().getId());
        assertEquals(BigDecimal.ZERO, result.getActualAmount());
        assertEquals(BigDecimal.ZERO, result.getAuthorizedAmount());

        verify(accountService, times(1)).getAccountById(accountId);
        verify(balanceRepository, times(1)).existsByAccountId(accountId);
        verify(balanceRepository, times(1)).save(any(Balance.class));
    }

    @Test
    void testCreateBalanceThrowWhenAlreadyExist() {
        when(accountService.getAccountById(accountId)).thenReturn(expectedAccount);
        when(balanceRepository.existsByAccountId(accountId)).thenReturn(true);

        assertThrows(PreConditionFailedException.class, () -> balanceService.createBalance(accountId));
        verify(accountService, times(1)).getAccountById(accountId);
        verify(balanceRepository, times(1)).existsByAccountId(accountId);
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void testEnrollAmount() {
        BigDecimal enrollAmount = new BigDecimal("100.00");

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(expectedBalance));
        when(balanceRepository.save(any(Balance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Balance updated = balanceService.enroll(accountId, enrollAmount);

        assertEquals(enrollAmount, updated.getActualAmount());
        assertEquals(BigDecimal.ZERO, updated.getAuthorizedAmount());
        verify(balanceRepository, times(1)).save(expectedBalance);
    }

    @Test
    void testEnrollInvalidAmount() {
        DataValidationException ex = assertThrows(DataValidationException.class, () ->
                balanceService.enroll(accountId, BigDecimal.ZERO));
        assertEquals("Amount must be positive!", ex.getMessage());
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void testAuthorize() {
        BigDecimal actualAmount = new BigDecimal("100");
        BigDecimal authorizeAmount = new BigDecimal("30");

        expectedBalance.setActualAmount(actualAmount);

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(expectedBalance));
        when(balanceRepository.save(any(Balance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Balance updated = balanceService.authorize(accountId, authorizeAmount);

        assertEquals(actualAmount.subtract(authorizeAmount), updated.getActualAmount());
        assertEquals(authorizeAmount, updated.getAuthorizedAmount());
    }

    @Test
    void testAuthorizeThrowWhenNotEnoughFunds() {
        BigDecimal actualAmount = new BigDecimal("100");
        BigDecimal authorizeAmount = new BigDecimal("101");

        expectedBalance.setActualAmount(actualAmount);

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(expectedBalance));

        assertThrows(PreConditionFailedException.class, () ->
                balanceService.authorize(accountId, authorizeAmount));
    }

    @Test
    void testCancelAuthorization() {
        BigDecimal actualAmount = new BigDecimal("100");
        BigDecimal authorizeAmount = new BigDecimal("50");
        BigDecimal cancelAuthorizeAmount = new BigDecimal("30");

        expectedBalance.setActualAmount(actualAmount);
        expectedBalance.setAuthorizedAmount(authorizeAmount);

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(expectedBalance));
        when(balanceRepository.save(any(Balance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Balance updated = balanceService.cancelAuthorization(accountId, cancelAuthorizeAmount);

        assertEquals(actualAmount.add(cancelAuthorizeAmount), updated.getActualAmount());
        assertEquals(authorizeAmount.subtract(cancelAuthorizeAmount), updated.getAuthorizedAmount());
    }

    @Test
    void testCancelAuthorizationThrowWhenNotEnoughFundsAuthorizedBefore() {
        BigDecimal actualAmount = new BigDecimal("100");
        BigDecimal authorizeAmount = new BigDecimal("30");
        BigDecimal cancelAuthorizeAmount = new BigDecimal("50");

        expectedBalance.setActualAmount(actualAmount);
        expectedBalance.setAuthorizedAmount(authorizeAmount);

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(expectedBalance));

        assertThrows(PreConditionFailedException.class, () ->
                balanceService.cancelAuthorization(accountId, cancelAuthorizeAmount));
    }

    @Test
    void testClearWhenEnoughAuthorized() {
        BigDecimal actualAmount = new BigDecimal("200");
        BigDecimal authorizeAmount = new BigDecimal("100");
        BigDecimal clearAmount = new BigDecimal("50");

        expectedBalance.setActualAmount(actualAmount);
        expectedBalance.setAuthorizedAmount(authorizeAmount);

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(expectedBalance));
        when(balanceRepository.save(any(Balance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Balance updated = balanceService.clear(accountId, clearAmount);

        assertEquals(authorizeAmount.subtract(clearAmount), updated.getAuthorizedAmount());
        assertEquals(actualAmount, updated.getActualAmount());
    }

    @Test
    void testClearThrowWhenNotEnoughAuthorized() {
        BigDecimal authorizeAmount = new BigDecimal("50");
        BigDecimal clearAmount = new BigDecimal("100");

        expectedBalance.setAuthorizedAmount(authorizeAmount);

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(expectedBalance));

        assertThrows(PreConditionFailedException.class, () ->
                balanceService.clear(accountId, clearAmount));
    }
}