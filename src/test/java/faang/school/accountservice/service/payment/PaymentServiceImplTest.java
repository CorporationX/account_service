package faang.school.accountservice.service.payment;

import faang.school.accountservice.dto.balance.BalanceUpdateDto;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.Payment;
import faang.school.accountservice.model.enums.PaymentStatus;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.repository.PaymentRepository;
import faang.school.accountservice.service.balance_audit.BalanceAuditService;
import faang.school.accountservice.validator.payment.PaymentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private PaymentValidator paymentValidator;

    @Mock
    private BalanceAuditService balanceAuditService;

    @Mock
    private BalanceAuditRepository balanceAuditRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private Balance senderBalance;
    private Balance receiverBalance;

    @Captor
    private ArgumentCaptor<BalanceUpdateDto> balanceUpdateCaptor;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setId(1L);
        payment.setSenderAccountNumber("123456");
        payment.setReceiverAccountNumber("654321");
        payment.setAmount(BigDecimal.valueOf(500));
        payment.setPaymentStatus(PaymentStatus.NEW);
        payment.setIdempotencyKey(UUID.randomUUID());

        Account senderAccount = new Account();
        senderAccount.setId(1L);
        senderBalance = new Balance();
        senderBalance.setId(1L);
        senderBalance.setAccount(senderAccount);
        senderBalance.setAuthorizationBalance(BigDecimal.valueOf(1000));
        senderBalance.setActualBalance(BigDecimal.valueOf(1000));

        Account receiverAccount = new Account();
        receiverAccount.setId(2L);
        receiverBalance = new Balance();
        receiverBalance.setId(2L);
        receiverBalance.setAccount(receiverAccount);
        receiverBalance.setAuthorizationBalance(BigDecimal.valueOf(1000));
        receiverBalance.setActualBalance(BigDecimal.valueOf(1000));
    }

    @Test
    void authorizePayment_PaymentNotFound_ThrowsException() {
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> paymentService.authorizePayment(1L, payment.getId()));
    }

    @Test
    void authorizePayment_ValidPayment_Success() {
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        when(balanceRepository.findBalanceByAccountNumber(payment.getSenderAccountNumber())).thenReturn(Optional.of(senderBalance));
        when(balanceRepository.findBalanceByAccountNumber(payment.getReceiverAccountNumber())).thenReturn(Optional.of(receiverBalance));

        paymentService.authorizePayment(1L, payment.getId());

        verify(paymentValidator).validateAuthorization(senderBalance, payment, 1L);
        verify(balanceRepository).save(senderBalance);
        verify(balanceRepository).save(receiverBalance);
        verify(paymentRepository).save(payment);
        verify(balanceAuditService, times(2)).createNewAudit(balanceUpdateCaptor.capture());

        BalanceUpdateDto senderAudit = balanceUpdateCaptor.getAllValues().get(0);
        BalanceUpdateDto receiverAudit = balanceUpdateCaptor.getAllValues().get(1);

        assertBalanceAudit(senderAudit, senderBalance, payment.getId());
        assertBalanceAudit(receiverAudit, receiverBalance, payment.getId());
    }

    @Test
    void cancelPayment_PaymentNotFound_ThrowsException() {
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> paymentService.cancelPayment(1L, payment.getId()));
    }

    @Test
    void cancelPayment_ValidPayment_Success() {
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        when(balanceRepository.findBalanceByAccountNumber(payment.getSenderAccountNumber())).thenReturn(Optional.of(senderBalance));
        when(balanceRepository.findBalanceByAccountNumber(payment.getReceiverAccountNumber())).thenReturn(Optional.of(receiverBalance));

        paymentService.cancelPayment(1L, payment.getId());

        verify(paymentValidator).validateCancelPayment(1L, payment, senderBalance);
        verify(balanceRepository).save(senderBalance);
        verify(balanceRepository).save(receiverBalance);
        verify(paymentRepository).save(payment);
        verify(balanceAuditService, times(2)).createNewAudit(balanceUpdateCaptor.capture());

        BalanceUpdateDto senderAudit = balanceUpdateCaptor.getAllValues().get(0);
        BalanceUpdateDto receiverAudit = balanceUpdateCaptor.getAllValues().get(1);

        assertBalanceAudit(senderAudit, senderBalance, payment.getId());
        assertBalanceAudit(receiverAudit, receiverBalance, payment.getId());
    }

    @Test
    void clearPayment_PaymentNotFound_ThrowsException() {
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> paymentService.clearPayment(payment.getId()));
    }

    @Test
    void clearPayment_ValidPayment_Success() {
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        when(balanceRepository.findBalanceByAccountNumber(payment.getSenderAccountNumber())).thenReturn(Optional.of(senderBalance));
        when(balanceRepository.findBalanceByAccountNumber(payment.getReceiverAccountNumber())).thenReturn(Optional.of(receiverBalance));

        paymentService.clearPayment(payment.getId());

        verify(paymentValidator).validateClearing(senderBalance, payment);
        verify(balanceRepository).save(senderBalance);
        verify(balanceRepository).save(receiverBalance);
        verify(paymentRepository).save(payment);
        verify(balanceAuditService, times(2)).createNewAudit(balanceUpdateCaptor.capture());

        BalanceUpdateDto senderAudit = balanceUpdateCaptor.getAllValues().get(0);
        BalanceUpdateDto receiverAudit = balanceUpdateCaptor.getAllValues().get(1);

        assertBalanceAudit(senderAudit, senderBalance, payment.getId());
        assertBalanceAudit(receiverAudit, receiverBalance, payment.getId());
    }

    private void assertBalanceAudit(BalanceUpdateDto audit, Balance balance, Long paymentId) {
        assertEquals(balance.getAccount().getId(), audit.getAccountId());
        assertEquals(balance.getAuthorizationBalance().longValue(), audit.getAuthorizedBalance());
        assertEquals(balance.getActualBalance().longValue(), audit.getActualBalance());
        assertEquals(paymentId, audit.getPaymentNumber());
    }
}