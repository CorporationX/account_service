package faang.school.accountservice.service;

import faang.school.accountservice.dto.PendingDto;
import faang.school.accountservice.dto.PendingStatus;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.publisher.PaymentStatusChangePublisher;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private BalanceService balanceService;

    @Mock
    private BalanceAuditService balanceAuditService;

    @Mock
    private PaymentStatusChangePublisher paymentStatusChangePublisher;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PendingDto pendingDto;
    private Balance fromBalance;
    private Balance toBalance;

    @BeforeEach
    void setUp() {
        fromBalance = Balance.builder()
                .curAuthBalance(BigDecimal.valueOf(50.00))
                .curFactBalance(BigDecimal.valueOf(100.00))
                .account(new Account())
                .build();
        toBalance = Balance.builder()
                .curAuthBalance(BigDecimal.valueOf(100.00))
                .curFactBalance(BigDecimal.valueOf(50.00))
                .account(new Account())
                .build();
        pendingDto = PendingDto.builder()
                .fromAccountId(1L)
                .toAccountId(2L)
                .amount(BigDecimal.valueOf(20.00))
                .status(PendingStatus.IN_PROGRESS)
                .build();
    }

    @Test
    void testCancelPayment_shouldUpdateBalancesAndPublishEvent() {
        when(balanceService.getBalanceWithAccountByAccountId(pendingDto.getFromAccountId())).thenReturn(fromBalance);
        when(balanceService.getBalanceWithAccountByAccountId(pendingDto.getToAccountId())).thenReturn(toBalance);

        PendingDto result = paymentService.cancelPayment(pendingDto);

        assertEquals(PendingStatus.CANCELED, result.getStatus());
        assertEquals(BigDecimal.valueOf(70.00), fromBalance.getCurAuthBalance());
        assertEquals(BigDecimal.valueOf(80.00), toBalance.getCurAuthBalance());

        verify(paymentStatusChangePublisher).publish(pendingDto);
        verify(balanceAuditService, times(2)).recordBalanceChange(
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(Balance.class)
        );
    }

    @Test
    void testCancelPayment_shouldRetryOnOptimisticLockException() {
        when(balanceService.getBalanceWithAccountByAccountId(pendingDto.getToAccountId()))
                .thenThrow(new OptimisticLockException("Test lock exception"));

        assertThrows(OptimisticLockException.class, () -> paymentService.cancelPayment(pendingDto));
    }

    @Test
    void testClearingPayment_successfulPayment() {
        when(balanceService.getBalanceWithAccountByAccountId(pendingDto.getFromAccountId())).thenReturn(fromBalance);
        when(balanceService.getBalanceWithAccountByAccountId(pendingDto.getToAccountId())).thenReturn(toBalance);

        PendingDto result = paymentService.clearingPayment(pendingDto);

        assertEquals(PendingStatus.SUCCESS, result.getStatus());
        assertEquals(BigDecimal.valueOf(80.00), fromBalance.getCurFactBalance());
        assertEquals(BigDecimal.valueOf(70.00), toBalance.getCurFactBalance());

        verify(paymentStatusChangePublisher).publish(pendingDto);
        verify(balanceAuditService, times(2)).recordBalanceChange(
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(Balance.class)
        );
    }

    @Test
    void testClearingPayment_shouldRetryOnOptimisticLockException() {
        when(balanceService.getBalanceWithAccountByAccountId(pendingDto.getFromAccountId()))
                .thenThrow(new OptimisticLockException("Test lock exception"));

        assertThrows(OptimisticLockException.class, () -> paymentService.clearingPayment(pendingDto));
    }
}

