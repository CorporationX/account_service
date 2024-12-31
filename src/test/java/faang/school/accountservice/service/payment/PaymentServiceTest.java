package faang.school.accountservice.service.payment;

import faang.school.accountservice.dto.payment.request.CancelPaymentRequest;
import faang.school.accountservice.dto.payment.request.ClearingPaymentRequest;
import faang.school.accountservice.dto.payment.request.ErrorPaymentRequest;
import faang.school.accountservice.dto.payment.request.PaymentRequest;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.payment.Payment;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.repository.PaymentRepository;
import faang.school.accountservice.validator.payment.PaymentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static faang.school.accountservice.enums.Currency.USD;
import static faang.school.accountservice.model.payment.Category.OTHER;
import static faang.school.accountservice.model.payment.PaymentStatus.ACTIVE;
import static faang.school.accountservice.model.payment.PaymentStatus.CLOSED;
import static faang.school.accountservice.model.payment.PaymentStatus.REJECTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    private static final Long SOURCE_ACCOUNT_ID = 2L;
    private static final Long TARGET_ACCOUNT_ID = 3L;
    private static final Long SOURCE_BALANCE_ID = 4L;
    private static final Long TARGET_BALANCE_ID = 5L;
    private static final UUID OPERATION_ID = UUID.randomUUID();
    private static final BigDecimal FIRST_AMOUNT = BigDecimal.valueOf(1.0);
    private static final BigDecimal SECOND_AMOUNT = BigDecimal.valueOf(1.0);

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentValidator paymentValidator;

    @InjectMocks
    private PaymentService paymentService;

    private Balance sourceBalance;
    private Balance targetBalance;
    private PaymentRequest paymentRequest;
    private Payment payment;
    private ClearingPaymentRequest clearingPaymentRequest;
    private CancelPaymentRequest cancelPaymentRequest;
    private ErrorPaymentRequest errorPaymentRequest;

    @BeforeEach
    public void setUp() {
        sourceBalance = Balance.builder()
                .id(SOURCE_BALANCE_ID)
                .authorizationBalance(BigDecimal.valueOf(0.0))
                .actualBalance(BigDecimal.valueOf(1.0))
                .build();

        targetBalance = Balance.builder()
                .id(TARGET_BALANCE_ID)
                .authorizationBalance(BigDecimal.valueOf(0))
                .actualBalance(BigDecimal.valueOf(0))
                .build();

        paymentRequest = PaymentRequest.builder()
                .operationId(OPERATION_ID)
                .sourceAccountId(SOURCE_ACCOUNT_ID)
                .targetAccountId(TARGET_ACCOUNT_ID)
                .amount(FIRST_AMOUNT)
                .currency(USD)
                .category(OTHER)
                .build();

        payment = Payment.builder().id(OPERATION_ID)
                .sourceBalance(sourceBalance)
                .targetBalance(targetBalance)
                .amount(SECOND_AMOUNT)
                .status(ACTIVE)
                .build();

        clearingPaymentRequest = ClearingPaymentRequest.builder()
                .operationId(OPERATION_ID)
                .build();

        cancelPaymentRequest = CancelPaymentRequest.builder()
                .operationId(OPERATION_ID)
                .build();

        errorPaymentRequest = ErrorPaymentRequest.builder()
                .operationId(OPERATION_ID)
                .build();
    }

    @Test
    void testAuthorizePayment_successful() {
        when(balanceRepository.findByAccountId(SOURCE_ACCOUNT_ID)).thenReturn(Optional.of(sourceBalance));
        when(balanceRepository.findByAccountId(TARGET_ACCOUNT_ID)).thenReturn(Optional.of(targetBalance));
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenReturn(payment);
        when(balanceRepository.saveAndFlush(any(Balance.class))).thenReturn(sourceBalance);

        paymentService.authorizePayment(paymentRequest);

        ArgumentCaptor<UUID> requestUuidCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Balance> sourceBalanceForValidateCaptor = ArgumentCaptor.forClass(Balance.class);

        verify(paymentValidator).checkFreeAmount(requestUuidCaptor.capture(), sourceBalanceForValidateCaptor.capture(),
                eq(FIRST_AMOUNT));

        ArgumentCaptor<Balance> sourceBalanceCaptor = ArgumentCaptor.forClass(Balance.class);
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        verify(balanceRepository).saveAndFlush(sourceBalanceCaptor.capture());
        verify(paymentRepository).saveAndFlush(paymentCaptor.capture());

        assertThat(requestUuidCaptor.getValue()).isEqualTo(OPERATION_ID);
        assertThat(sourceBalanceForValidateCaptor.getValue()).isEqualTo(sourceBalance);

        Balance resultSourceBalance = sourceBalanceCaptor.getValue();
        BigDecimal expectedAuthorizationBalance = BigDecimal.valueOf(0.0).add(FIRST_AMOUNT);
        BigDecimal expectedActualBalance = BigDecimal.valueOf(1.0).subtract(FIRST_AMOUNT);

        assertThat(resultSourceBalance.getAuthorizationBalance()).isEqualByComparingTo(expectedAuthorizationBalance);
        assertThat(resultSourceBalance.getActualBalance()).isEqualByComparingTo(expectedActualBalance);

        Payment resultPayment = paymentCaptor.getValue();
        assertThat(resultPayment.getSourceBalance()).isEqualTo(sourceBalance);
        assertThat(resultPayment.getTargetBalance()).isEqualTo(targetBalance);
        assertThat(resultPayment.getAmount()).isEqualByComparingTo(FIRST_AMOUNT);
        assertThat(resultPayment.getStatus()).isEqualTo(ACTIVE);
    }

    @Test
    void testClearingPayment_successful() {
        sourceBalance.setAuthorizationBalance(BigDecimal.valueOf(2));
        sourceBalance.setActualBalance(BigDecimal.valueOf(2));
        targetBalance.setAuthorizationBalance(BigDecimal.ZERO);
        targetBalance.setActualBalance(BigDecimal.ZERO);

        when(paymentRepository.findById(OPERATION_ID)).thenReturn(Optional.of(payment));
        when(balanceRepository.saveAndFlush(any(Balance.class))).thenReturn(sourceBalance, targetBalance);
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenReturn(payment);

        paymentService.clearingPayment(clearingPaymentRequest);

        ArgumentCaptor<Payment> paymentForValidCaptor = ArgumentCaptor.forClass(Payment.class);

        verify(paymentValidator).checkAuthPaymentStatus(paymentForValidCaptor.capture());

        ArgumentCaptor<Balance> balancesCaptor = ArgumentCaptor.forClass(Balance.class);
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        verify(balanceRepository, times(2)).saveAndFlush(balancesCaptor.capture());
        verify(paymentRepository).saveAndFlush(paymentCaptor.capture());

        assertThat(paymentForValidCaptor.getValue()).isEqualTo(payment);

        Balance resultSourceBalance = balancesCaptor.getAllValues().get(0);
        BigDecimal expectedSourceAuthorizationBalance = BigDecimal.valueOf(2).subtract(SECOND_AMOUNT);
        BigDecimal expectedSourceActualBalance = BigDecimal.valueOf(2.0);

        Balance resultTargetBalance = balancesCaptor.getAllValues().get(1);
        BigDecimal expectedTargetAuthorizationBalance = BigDecimal.ZERO;
        BigDecimal expectedTargetActualBalance = BigDecimal.ZERO.add(SECOND_AMOUNT);

        assertThat(resultSourceBalance.getAuthorizationBalance()).isEqualByComparingTo(expectedSourceAuthorizationBalance);
        assertThat(resultSourceBalance.getActualBalance()).isEqualByComparingTo(expectedSourceActualBalance);

        assertThat(resultTargetBalance.getAuthorizationBalance()).isEqualByComparingTo(expectedTargetAuthorizationBalance);
        assertThat(resultTargetBalance.getActualBalance()).isEqualByComparingTo(expectedTargetActualBalance);

        Payment paymentResult = paymentCaptor.getValue();
        assertThat(paymentResult.getAmount()).isEqualByComparingTo(SECOND_AMOUNT);
        assertThat(paymentResult.getStatus()).isEqualTo(CLOSED);
    }

    @Test
    void testCancelPayment_successful() {
        sourceBalance.setAuthorizationBalance(BigDecimal.valueOf(2));
        sourceBalance.setActualBalance(BigDecimal.valueOf(2));

        when(paymentRepository.findById(OPERATION_ID)).thenReturn(Optional.of(payment));
        when(balanceRepository.saveAndFlush(any(Balance.class))).thenReturn(sourceBalance);
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenReturn(payment);

        paymentService.cancelPayment(cancelPaymentRequest);

        ArgumentCaptor<Payment> authPaymentForValidCaptor = ArgumentCaptor.forClass(Payment.class);

        verify(paymentValidator).checkAuthPaymentStatus(authPaymentForValidCaptor.capture());

        ArgumentCaptor<Balance> sourceBalanceCaptor = ArgumentCaptor.forClass(Balance.class);
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        verify(balanceRepository).saveAndFlush(sourceBalanceCaptor.capture());
        verify(paymentRepository).saveAndFlush(paymentCaptor.capture());

        Balance resultSourceBalance = sourceBalanceCaptor.getValue();
        BigDecimal expectedAuthorizationBalance = BigDecimal.valueOf(2).subtract(SECOND_AMOUNT);
        BigDecimal expectedActualBalance = BigDecimal.valueOf(2).add(SECOND_AMOUNT);

        assertThat(resultSourceBalance.getAuthorizationBalance()).isEqualByComparingTo(expectedAuthorizationBalance);
        assertThat(resultSourceBalance.getActualBalance()).isEqualByComparingTo(expectedActualBalance);

        Payment resultPayment = paymentCaptor.getValue();
        assertThat(resultPayment.getStatus()).isEqualTo(REJECTED);
    }

    @Test
    void testErrorPayment_successful() {
        sourceBalance.setAuthorizationBalance(BigDecimal.valueOf(2));
        sourceBalance.setActualBalance(BigDecimal.valueOf(2));

        when(paymentRepository.findById(OPERATION_ID)).thenReturn(Optional.of(payment));
        when(balanceRepository.saveAndFlush(any(Balance.class))).thenReturn(sourceBalance);
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenReturn(payment);

        paymentService.errorPayment(errorPaymentRequest);

        ArgumentCaptor<Payment> authPaymentForValidCaptor = ArgumentCaptor.forClass(Payment.class);

        verify(paymentValidator).checkAuthPaymentStatus(authPaymentForValidCaptor.capture());

        ArgumentCaptor<Balance> sourceBalanceCaptor = ArgumentCaptor.forClass(Balance.class);
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        verify(balanceRepository).saveAndFlush(sourceBalanceCaptor.capture());
        verify(paymentRepository).saveAndFlush(paymentCaptor.capture());

        Balance resultSourceBalance = sourceBalanceCaptor.getValue();
        BigDecimal expectedAuthorizationBalance = BigDecimal.valueOf(2).subtract(SECOND_AMOUNT);
        BigDecimal expectedActualBalance = BigDecimal.valueOf(2).add(SECOND_AMOUNT);

        assertThat(resultSourceBalance.getAuthorizationBalance()).isEqualByComparingTo(expectedAuthorizationBalance);
        assertThat(resultSourceBalance.getActualBalance()).isEqualByComparingTo(expectedActualBalance);

        Payment resultPayment = paymentCaptor.getValue();
        assertThat(resultPayment.getStatus()).isEqualTo(REJECTED);
    }
}