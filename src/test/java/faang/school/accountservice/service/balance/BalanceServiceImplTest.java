package faang.school.accountservice.service.balance;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.ConcurrentModificationException;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.service.AccountService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {

    @Mock
    private AccountService accountService;

    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private final Long accountId = 1L;
    private final BigDecimal amount = BigDecimal.valueOf(100);
    private Balance balance;

    @BeforeEach
    void setUp() {
        balance = new Balance();
        balance.setActualBalance(BigDecimal.valueOf(500));
        balance.setAuthorizedBalance(BigDecimal.ZERO);
    }

    @Test
    void testGetBalanceByAccountIdWhenBalanceExistsReturnsBalance() {
        Balance expectedBalance = new Balance();
        when(balanceRepository.findByAccountId(1L)).thenReturn(Optional.of(expectedBalance));

        Balance result = balanceService.getBalanceByAccountId(1L);

        assertEquals(expectedBalance, result);
        verify(balanceRepository).findByAccountId(1L);
    }

    @Test
    void testGetBalanceByAccountIdWhenBalanceNotExistsThrowsException() {
        when(balanceRepository.findByAccountId(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            balanceService.getBalanceByAccountId(1L);
        });
        verify(balanceRepository).findByAccountId(1L);
    }

    @Test
    void testCreateBalanceWithValidDataCreatesNewBalance() {
        Account account = new Account();
        when(accountService.getAccountEntity(1L)).thenReturn(account);
        when(balanceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Balance result = balanceService.createBalance(1L, BigDecimal.valueOf(100));

        assertNotNull(result);
        assertEquals(account, result.getAccount());
        assertEquals(BigDecimal.valueOf(100), result.getActualBalance());
        assertEquals(BigDecimal.ZERO, result.getAuthorizedBalance());
    }

    @Test
    void testCreateBalanceWithNegativeAmountThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            balanceService.createBalance(1L, BigDecimal.valueOf(-1));
        });
    }

    @Test
    void testAuthorizeSuccessfulAuthorization() {
        Balance balance = new Balance();
        balance.setActualBalance(BigDecimal.valueOf(500));
        balance.setAuthorizedBalance(BigDecimal.ZERO);

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(balance)).thenReturn(balance);

        Balance result = balanceService.authorize(accountId, amount);

        assertEquals(BigDecimal.valueOf(400), result.getActualBalance());
        assertEquals(amount, result.getAuthorizedBalance());
        verify(balanceRepository).save(balance);
    }

    @Test
    void testAuthorizeNegativeAmountThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> balanceService.authorize(accountId, BigDecimal.valueOf(-1)));

        assertEquals("Authorization amount must be positive", exception.getMessage());
        verifyNoInteractions(balanceRepository);
    }

    @Test
    void testAuthorizeZeroAmountThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> balanceService.authorize(accountId, BigDecimal.ZERO));
    }

    @Test
    void testAuthorizeInsufficientFundsThrowsException() {
        Balance balance = new Balance();
        balance.setActualBalance(BigDecimal.TEN);

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> balanceService.authorize(accountId, amount));

        assertEquals("Insufficient actual funds", exception.getMessage());
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void testAuthorizeBalanceNotFoundThrowsException() {
        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> balanceService.authorize(accountId, amount));
    }

    @Test
    void testClearSuccessfulClear() {
        Balance balance = new Balance();
        balance.setAuthorizedBalance(BigDecimal.valueOf(150));

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(balance)).thenReturn(balance);

        Balance result = balanceService.clear(accountId, amount);

        assertEquals(BigDecimal.valueOf(50), result.getAuthorizedBalance());
        verify(balanceRepository).save(balance);
    }

    @Test
    void testClearNegativeAmountThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> balanceService.clear(accountId, BigDecimal.valueOf(-1)));

        assertEquals("Clear amount must be positive", exception.getMessage());
        verifyNoInteractions(balanceRepository);
    }

    @Test
    void testClearZeroAmountThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> balanceService.clear(accountId, BigDecimal.ZERO));
    }

    @Test
    void testClearInsufficientAuthorizedFundsThrowsException() {
        Balance balance = new Balance();
        balance.setAuthorizedBalance(BigDecimal.TEN);

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> balanceService.clear(accountId, amount));

        assertEquals("Insufficient authorized funds", exception.getMessage());
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void testClearBalanceNotFoundThrowsException() {
        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> balanceService.clear(accountId, amount));
    }

    @Test
    void testCancelAuthorizationSuccessfulCancellation() {
        Balance balance = new Balance();
        balance.setAuthorizedBalance(BigDecimal.valueOf(150));
        balance.setActualBalance(BigDecimal.valueOf(200));

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(balance)).thenReturn(balance);

        Balance result = balanceService.cancelAuthorization(accountId, amount);

        assertEquals(BigDecimal.valueOf(50), result.getAuthorizedBalance());
        assertEquals(BigDecimal.valueOf(300), result.getActualBalance());
        verify(balanceRepository).save(balance);
    }

    @Test
    void testCancelAuthorizationNegativeAmountThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> balanceService.cancelAuthorization(accountId, BigDecimal.valueOf(-1)));

        assertEquals("Cancel amount must be positive", exception.getMessage());
        verifyNoInteractions(balanceRepository);
    }

    @Test
    void testCancelAuthorizationZeroAmountThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> balanceService.cancelAuthorization(accountId, BigDecimal.ZERO));
    }

    @Test
    void testCancelAuthorizationInsufficientAuthorizedFundsThrowsException() {
        Balance balance = new Balance();
        balance.setAuthorizedBalance(BigDecimal.TEN);
        balance.setActualBalance(BigDecimal.valueOf(500));

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> balanceService.cancelAuthorization(accountId, amount));

        assertEquals("Insufficient authorized funds to cancel", exception.getMessage());
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void testCancelAuthorizationBalanceNotFoundThrowsException() {
        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> balanceService.cancelAuthorization(accountId, amount));
    }

    @Test
    void testCancelAuthorizationFullAmountCancellation() {
        Balance balance = new Balance();
        balance.setAuthorizedBalance(amount);
        balance.setActualBalance(BigDecimal.valueOf(500));

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(balance)).thenReturn(balance);

        Balance result = balanceService.cancelAuthorization(accountId, amount);

        assertEquals(BigDecimal.ZERO, result.getAuthorizedBalance());
        assertEquals(BigDecimal.valueOf(600), result.getActualBalance());
    }

    @Test
    void testCancelAuthorizationVerifyActualBalanceCalculation() {
        Balance balance = new Balance();
        balance.setAuthorizedBalance(BigDecimal.valueOf(150));
        balance.setActualBalance(BigDecimal.valueOf(250));

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(balance)).thenReturn(balance);

        Balance result = balanceService.cancelAuthorization(accountId, BigDecimal.valueOf(70));

        assertEquals(BigDecimal.valueOf(80), result.getAuthorizedBalance());
        assertEquals(BigDecimal.valueOf(320), result.getActualBalance());
    }

    @Test
    void testReplenishSuccessfulReplenishment() {
        Balance balance = new Balance();
        balance.setActualBalance(BigDecimal.valueOf(500));

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(balance)).thenReturn(balance);

        Balance result = balanceService.replenish(accountId, amount);

        assertEquals(BigDecimal.valueOf(600), result.getActualBalance());
        verify(balanceRepository).save(balance);
    }

    @Test
    void testReplenishNegativeAmountThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> balanceService.replenish(accountId, BigDecimal.valueOf(-1)));

        assertEquals("Replenishment amount must be positive", exception.getMessage());
        verifyNoInteractions(balanceRepository);
    }

    @Test
    void testReplenishZeroAmountThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> balanceService.replenish(accountId, BigDecimal.ZERO));
    }

    @Test
    void testReplenishBalanceNotFoundThrowsException() {
        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> balanceService.replenish(accountId, amount));
    }

    @Test
    void testReplenishVerifyDecimalPrecision() {
        Balance balance = new Balance();
        balance.setActualBalance(new BigDecimal("500.50"));

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(balance)).thenReturn(balance);

        Balance result = balanceService.replenish(accountId, new BigDecimal("99.99"));

        assertEquals(new BigDecimal("600.49"), result.getActualBalance());
    }

    @Test
    void testReplenishFromZeroBalance() {
        Balance balance = new Balance();
        balance.setActualBalance(BigDecimal.ZERO);

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(balance)).thenReturn(balance);

        Balance result = balanceService.replenish(accountId, amount);

        assertEquals(amount, result.getActualBalance());
    }

    @Test
    void testReplenishLargeAmount() {
        Balance balance = new Balance();
        balance.setActualBalance(BigDecimal.valueOf(1_000_000));

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(balance)).thenReturn(balance);

        Balance result = balanceService.replenish(accountId, BigDecimal.valueOf(500_000));

        assertEquals(BigDecimal.valueOf(1_500_000), result.getActualBalance());
    }

    @Test
    void testRecoverWithOptimisticLockingFailureExceptionShouldHandleCorrectly() {
        OptimisticLockException exception = new OptimisticLockException("Test optimistic lock");

        ConcurrentModificationException thrown = assertThrows(
                ConcurrentModificationException.class,
                () -> balanceService.recover(exception, accountId, amount)
        );

        assertEquals("Failed to update balance due to concurrent access", thrown.getMessage());
    }

    @Test
    void testRecoverWithNullExceptionShouldThrowWithNullCause() {
        ConcurrentModificationException thrown = assertThrows(
                ConcurrentModificationException.class,
                () -> balanceService.recover(null, accountId, amount)
        );

        assertEquals("Failed to update balance due to concurrent access", thrown.getMessage());
        assertNull(thrown.getCause());
    }

    @Test
    void testRecoverWithNullArgumentsShouldStillWork() {
        OptimisticLockException exception = new OptimisticLockException("Test");

        assertThrows(
                ConcurrentModificationException.class,
                () -> balanceService.recover(exception, null, null)
        );
    }
}