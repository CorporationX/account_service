package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.exception.BalanceInvariantViolationException;
import faang.school.accountservice.exception.DuplicateEntityException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.InsufficientFundsException;
import faang.school.accountservice.exception.ServiceUnavailableException;
import faang.school.accountservice.mapper.BalanceUpdateMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {

    private static final BigDecimal AMOUNT_100 = new BigDecimal("100.00");
    private static final BigDecimal AMOUNT_10 = new BigDecimal("10.00");
    private static final BigDecimal AMOUNT_20 = new BigDecimal("20.00");
    private static final BigDecimal AMOUNT_30 = new BigDecimal("30.00");
    private static final BigDecimal AMOUNT_50 = new BigDecimal("50.00");
    private static final BigDecimal AMOUNT_200 = new BigDecimal("200.00");
    private static final BigDecimal NEGATIVE_ONE = new BigDecimal("-1.00");

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BalanceUpdateMapper balanceUpdateMapper;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private UUID accountId;
    private Account account;
    private Balance balance;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        account = new Account();
        account.setId(accountId);

        balance = Balance.builder()
            .id(1L)
            .account(account)
            .authBalance(ZERO)
            .actualBalance(AMOUNT_100)
            .build();
    }

    @Test
    void getBalance_whenBalanceExists_returnsBalance() {
        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));

        Balance result = balanceService.getBalance(accountId);

        assertSame(balance, result);
        verify(balanceRepository).findByAccountId(accountId);
    }

    @Test
    void getBalance_whenBalanceNotFound_throwsEntityNotFound() {
        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> balanceService.getBalance(accountId));

        verify(balanceRepository).findByAccountId(accountId);
    }

    @Test
    void authorize_whenEnoughAvailableFunds_increasesAuthBalanceOnly() {
        when(balanceRepository.findAndLockByAccountId(accountId)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(any(Balance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Balance result = balanceService.authorize(accountId, AMOUNT_30);

        assertEquals(AMOUNT_30, result.getAuthBalance(), "authBalance must increase by amountToAuthorize");
        assertEquals(AMOUNT_100, result.getActualBalance(), "actualBalance must NOT change on authorize");

        verify(balanceRepository).findAndLockByAccountId(accountId);
        verify(balanceRepository).save(balance);
    }

    @Test
    void authorize_whenAmountNegative_throwsIllegalArgumentException_andDoesNotHitRepository() {
        assertThrows(IllegalArgumentException.class, () -> balanceService.authorize(accountId, NEGATIVE_ONE));
        verifyNoInteractions(balanceRepository);
    }

    @Test
    void authorize_whenInsufficientAvailableFunds_throwsInsufficientFundsException() {
        balance.setActualBalance(AMOUNT_10);
        when(balanceRepository.findAndLockByAccountId(accountId)).thenReturn(Optional.of(balance));

        assertThrows(InsufficientFundsException.class, () -> balanceService.authorize(accountId, AMOUNT_20));

        verify(balanceRepository).findAndLockByAccountId(accountId);
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void authorize_whenBalanceNotFound_throwsEntityNotFound() {
        when(balanceRepository.findAndLockByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> balanceService.authorize(accountId, AMOUNT_10));

        verify(balanceRepository).findAndLockByAccountId(accountId);
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void authorize_whenExistingAuthReducesAvailable_thenInsufficient() {
        balance.setAuthBalance(new BigDecimal("90.00"));
        when(balanceRepository.findAndLockByAccountId(accountId)).thenReturn(Optional.of(balance));

        assertThrows(InsufficientFundsException.class, () -> balanceService.authorize(accountId, AMOUNT_20));

        verify(balanceRepository).findAndLockByAccountId(accountId);
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void topUpActualBalance_whenValidAmount_increasesActualBalance() {
        when(balanceRepository.findAndLockByAccountId(accountId)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(any(Balance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Balance result = balanceService.topUpActualBalance(accountId, AMOUNT_50);

        assertEquals(AMOUNT_100.add(AMOUNT_50), result.getActualBalance());
        assertEquals(ZERO, result.getAuthBalance());

        verify(balanceRepository).findAndLockByAccountId(accountId);
        verify(balanceRepository).save(balance);
    }

    @Test
    void topUpActualBalance_whenAmountNegative_throwsIllegalArgumentException_andDoesNotHitRepository() {
        assertThrows(IllegalArgumentException.class, () -> balanceService.topUpActualBalance(accountId, NEGATIVE_ONE));
        verifyNoInteractions(balanceRepository);
    }

    @Test
    void topUpActualBalance_whenBalanceNotFound_throwsEntityNotFound() {
        when(balanceRepository.findAndLockByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> balanceService.topUpActualBalance(accountId, AMOUNT_10));

        verify(balanceRepository).findAndLockByAccountId(accountId);
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void withdrawActualBalance_whenEnoughAvailable_decreasesActualBalance() {
        when(balanceRepository.findAndLockByAccountId(accountId)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(any(Balance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Balance result = balanceService.withdrawActualBalance(accountId, AMOUNT_30);

        assertEquals(AMOUNT_100.subtract(AMOUNT_30), result.getActualBalance());
        assertEquals(ZERO, result.getAuthBalance());

        verify(balanceRepository).findAndLockByAccountId(accountId);
        verify(balanceRepository).save(balance);
    }

    @Test
    void withdrawActualBalance_whenAmountNegative_throwsIllegalArgumentException_andDoesNotHitRepository() {
        assertThrows(IllegalArgumentException.class,
                     () -> balanceService.withdrawActualBalance(accountId, NEGATIVE_ONE));
        verifyNoInteractions(balanceRepository);
    }

    @Test
    void withdrawActualBalance_whenInsufficientAvailableDueToHold_throwsInsufficientFundsException() {
        balance.setAuthBalance(new BigDecimal("90.00"));
        when(balanceRepository.findAndLockByAccountId(accountId)).thenReturn(Optional.of(balance));

        assertThrows(InsufficientFundsException.class,
                     () -> balanceService.withdrawActualBalance(accountId, AMOUNT_20));

        verify(balanceRepository).findAndLockByAccountId(accountId);
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void withdrawActualBalance_whenBalanceNotFound_throwsEntityNotFound() {
        when(balanceRepository.findAndLockByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> balanceService.withdrawActualBalance(accountId, AMOUNT_10));

        verify(balanceRepository).findAndLockByAccountId(accountId);
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void clearing_whenAuthEnough_setsAuthToZero_andDecreasesActualByAmount() {
        balance.setAuthBalance(AMOUNT_50);
        when(balanceRepository.findAndLockByAccountId(accountId)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(any(Balance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Balance result = balanceService.clearing(accountId, AMOUNT_20);

        assertEquals(ZERO, result.getAuthBalance());
        assertEquals(AMOUNT_100.subtract(AMOUNT_20), result.getActualBalance());

        verify(balanceRepository).findAndLockByAccountId(accountId);
        verify(balanceRepository).save(balance);
    }

    @Test
    void clearing_whenAuthLessThanAmount_throwsInsufficientFundsException() {
        balance.setAuthBalance(AMOUNT_10);
        when(balanceRepository.findAndLockByAccountId(accountId)).thenReturn(Optional.of(balance));

        assertThrows(InsufficientFundsException.class, () -> balanceService.clearing(accountId, AMOUNT_20));

        verify(balanceRepository).findAndLockByAccountId(accountId);
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void clearing_whenAmountNegative_throwsIllegalArgumentException_andDoesNotHitRepository() {
        assertThrows(IllegalArgumentException.class, () -> balanceService.clearing(accountId, NEGATIVE_ONE));
        verifyNoInteractions(balanceRepository);
    }

    @Test
    void clearing_whenBalanceNotFound_throwsEntityNotFound() {
        when(balanceRepository.findAndLockByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> balanceService.clearing(accountId, AMOUNT_10));

        verify(balanceRepository).findAndLockByAccountId(accountId);
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void update_whenBothAuthAndActualNull_throwsBalanceInvariantViolationException() {
        UpdateBalanceDto dto = new UpdateBalanceDto(null, null);

        assertThrows(BalanceInvariantViolationException.class, () -> balanceService.update(accountId, dto));

        verifyNoInteractions(balanceRepository, balanceUpdateMapper);
    }

    @Test
    void update_whenNegativeAuthBalance_throwsIllegalArgumentException_andDoesNotHitRepository() {
        UpdateBalanceDto dto = new UpdateBalanceDto(NEGATIVE_ONE, null);

        assertThrows(IllegalArgumentException.class, () -> balanceService.update(accountId, dto));

        verifyNoInteractions(balanceRepository, balanceUpdateMapper);
    }

    @Test
    void update_whenNegativeActualBalance_throwsIllegalArgumentException_andDoesNotHitRepository() {
        UpdateBalanceDto dto = new UpdateBalanceDto(null, NEGATIVE_ONE);

        assertThrows(IllegalArgumentException.class, () -> balanceService.update(accountId, dto));

        verifyNoInteractions(balanceRepository, balanceUpdateMapper);
    }

    @Test
    void update_whenBalanceNotFound_throwsEntityNotFound() {
        UpdateBalanceDto dto = new UpdateBalanceDto(AMOUNT_10, null);
        when(balanceRepository.findAndLockByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> balanceService.update(accountId, dto));

        verify(balanceRepository).findAndLockByAccountId(accountId);
        verifyNoInteractions(balanceUpdateMapper);
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void update_whenActualNull_andNewAuthGreaterThanCurrentActual_throwsInvariantViolation() {
        UpdateBalanceDto dto = new UpdateBalanceDto(AMOUNT_200, null);
        when(balanceRepository.findAndLockByAccountId(accountId)).thenReturn(Optional.of(balance));

        assertThrows(BalanceInvariantViolationException.class, () -> balanceService.update(accountId, dto));

        verify(balanceRepository).findAndLockByAccountId(accountId);
        verifyNoInteractions(balanceUpdateMapper);
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void update_whenAuthNull_andNewActualLessThanCurrentAuth_throwsInvariantViolation() {
        balance.setAuthBalance(AMOUNT_50);
        UpdateBalanceDto dto = new UpdateBalanceDto(null, AMOUNT_20);
        when(balanceRepository.findAndLockByAccountId(accountId)).thenReturn(Optional.of(balance));

        assertThrows(BalanceInvariantViolationException.class, () -> balanceService.update(accountId, dto));

        verify(balanceRepository).findAndLockByAccountId(accountId);
        verifyNoInteractions(balanceUpdateMapper);
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void update_whenBothProvided_andNewActualLessThanNewAuth_throwsInvariantViolation() {
        UpdateBalanceDto dto = new UpdateBalanceDto(AMOUNT_20, AMOUNT_10);
        when(balanceRepository.findAndLockByAccountId(accountId)).thenReturn(Optional.of(balance));

        assertThrows(BalanceInvariantViolationException.class, () -> balanceService.update(accountId, dto));

        verify(balanceRepository).findAndLockByAccountId(accountId);
        verifyNoInteractions(balanceUpdateMapper);
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void update_whenValidDto_callsMapperAndSaves() {
        UpdateBalanceDto dto = new UpdateBalanceDto(AMOUNT_10, null);

        when(balanceRepository.findAndLockByAccountId(accountId)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(any(Balance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Balance result = balanceService.update(accountId, dto);

        assertSame(balance, result);
        verify(balanceRepository).findAndLockByAccountId(accountId);
        verify(balanceUpdateMapper).updateBalanceFromDto(dto, balance);
        verify(balanceRepository).save(balance);
    }

    @Test
    void create_whenValidData_createsBalanceAndSaves() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(balanceRepository.save(any(Balance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Balance result = balanceService.create(accountId, AMOUNT_200);

        ArgumentCaptor<Balance> captor = ArgumentCaptor.forClass(Balance.class);
        verify(balanceRepository).save(captor.capture());

        Balance saved = captor.getValue();
        assertSame(result, saved);
        assertSame(account, saved.getAccount());
        assertEquals(AMOUNT_200, saved.getActualBalance());

        BigDecimal auth = saved.getAuthBalance() == null
            ? ZERO
            : saved.getAuthBalance();
        assertEquals(ZERO, auth);

        verify(accountRepository).findById(accountId);
    }

    @Test
    void create_whenAmountNegative_throwsIllegalArgumentException_andDoesNotHitRepositories() {
        assertThrows(IllegalArgumentException.class, () -> balanceService.create(accountId, NEGATIVE_ONE));
        verifyNoInteractions(accountRepository, balanceRepository);
    }

    @Test
    void create_whenAccountNotFound_throwsEntityNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> balanceService.create(accountId, AMOUNT_100));

        verify(accountRepository).findById(accountId);
        verifyNoInteractions(balanceRepository);
    }

    @Test
    void create_whenDuplicateBalance_throwsDuplicateEntityException() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(balanceRepository.save(any(Balance.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThrows(DuplicateEntityException.class, () -> balanceService.create(accountId, AMOUNT_100));

        verify(accountRepository).findById(accountId);
        verify(balanceRepository).save(any(Balance.class));
    }

    @Test
    void recover_whenPessimisticLockingFailure_throwsServiceUnavailableExceptionWithCause() {
        BalanceServiceImpl service = new BalanceServiceImpl(null, null, null);

        UUID accountId = UUID.randomUUID();
        PessimisticLockingFailureException cause =
            new PessimisticLockingFailureException("lock timeout / could not obtain lock");

        ServiceUnavailableException ex = assertThrows(
            ServiceUnavailableException.class,
            () -> service.recover(cause, accountId)
                                                     );
        assertTrue(ex.getMessage().contains(accountId.toString()));
        assertSame(cause, ex.getCause());
    }
}