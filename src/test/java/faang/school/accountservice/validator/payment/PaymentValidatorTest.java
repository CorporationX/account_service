package faang.school.accountservice.validator.payment;

import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.Payment;
import faang.school.accountservice.model.enums.PaymentStatus;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentValidatorTest {

    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private PaymentValidator paymentValidator;

    private Balance senderBalance;
    private Payment payment;

    @BeforeEach
    void setUp() {
        senderBalance = new Balance();
        senderBalance.setId(1L);
        senderBalance.setAuthorizationBalance(BigDecimal.valueOf(1000));
        senderBalance.setActualBalance(BigDecimal.valueOf(1000));

        payment = new Payment();
        payment.setId(1L);
        payment.setAmount(BigDecimal.valueOf(500));
        payment.setPaymentStatus(PaymentStatus.NEW);
    }

    @Test
    void validateAuthorization_UserNotBalanceOwner_ThrowsException() {
        when(balanceRepository.isBalanceOwner(senderBalance.getId(), 1L)).thenReturn(false);

        assertThrows(DataValidationException.class, () ->
                paymentValidator.validateAuthorization(senderBalance, payment, 1L));
    }

    @Test
    void validateAuthorization_NotEnoughAuthorizationBalance_ThrowsException() {
        when(balanceRepository.isBalanceOwner(senderBalance.getId(), 1L)).thenReturn(true);
        payment.setAmount(BigDecimal.valueOf(1500));

        assertThrows(DataValidationException.class, () ->
                paymentValidator.validateAuthorization(senderBalance, payment, 1L));
    }

    @Test
    void validateAuthorization_IncorrectPaymentStatus_ThrowsException() {
        when(balanceRepository.isBalanceOwner(senderBalance.getId(), 1L)).thenReturn(true);
        payment.setPaymentStatus(PaymentStatus.READY_TO_CLEAR);

        assertThrows(DataValidationException.class, () ->
                paymentValidator.validateAuthorization(senderBalance, payment, 1L));
    }

    @Test
    void validateCancelPayment_InvalidStatus_ThrowsException() {
        payment.setPaymentStatus(PaymentStatus.CLEAR);

        assertThrows(DataValidationException.class, () ->
                paymentValidator.validateCancelPayment(1L, payment, senderBalance));
    }

    @Test
    void validateCancelPayment_UserNotBalanceOwner_ThrowsException() {
        when(balanceRepository.isBalanceOwner(senderBalance.getId(), 1L)).thenReturn(false);

        assertThrows(DataValidationException.class, () ->
                paymentValidator.validateCancelPayment(1L, payment, senderBalance));
    }

    @Test
    void validateClearing_NotEnoughActualBalance_ThrowsException() {
        payment.setAmount(BigDecimal.valueOf(1500));

        assertThrows(DataValidationException.class, () ->
                paymentValidator.validateClearing(senderBalance, payment));
    }

    @Test
    void validateClearing_IncorrectPaymentStatus_ThrowsException() {
        payment.setPaymentStatus(PaymentStatus.NEW);

        assertThrows(DataValidationException.class, () ->
                paymentValidator.validateClearing(senderBalance, payment));
    }
}