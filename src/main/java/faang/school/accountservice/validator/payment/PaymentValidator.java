package faang.school.accountservice.validator.payment;

import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.Payment;
import faang.school.accountservice.model.enums.PaymentStatus;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentValidator {
    private final BalanceRepository balanceRepository;

    public void validateAuthorization(Balance senderBalance, Payment payment, Long userId) {
        validateUserIsBalanceOwner(senderBalance.getId(), userId);
        validateSenderHaveEnoughMoneyOnAuthorizationBalance(senderBalance, payment);
        validatePaymentStatusIsAlreadyCorrect(payment, PaymentStatus.READY_TO_CLEAR);
        validateStatus(payment, PaymentStatus.NEW);
    }

    public void validateCancelPayment(Long userId, Payment payment, Balance senderBalance) {
        validatePaymentStatusForCancel(payment);
        validateUserIsBalanceOwner(senderBalance.getId(), userId);
    }

    public void validateClearing(Balance senderBalance, Payment payment) {
        validateSenderHaveEnoughMoneyOnActualBalance(senderBalance, payment);
        validatePaymentStatusIsAlreadyCorrect(payment, PaymentStatus.CLEAR);
        validateStatus(payment, PaymentStatus.READY_TO_CLEAR);
    }

    private void validateUserIsBalanceOwner(Long balanceId, Long userId) {
        if (!balanceRepository.isBalanceOwner(balanceId, userId)) {
            throw new DataValidationException(
                    String.format("Only balance owner could send money from balance %s", balanceId));
        }
    }

    private void validateSenderHaveEnoughMoneyOnAuthorizationBalance(Balance senderBalance, Payment payment) {
        if (senderBalance.getAuthorizationBalance().compareTo(payment.getAmount()) < 0) {
            throw new DataValidationException("Not enough money on authorization balance");
        }
    }

    private void validateSenderHaveEnoughMoneyOnActualBalance(Balance senderBalance, Payment payment) {
        if (senderBalance.getAuthorizationBalance().compareTo(payment.getAmount()) < 0) {
            throw new DataValidationException("Not enough money on actual balance");
        }
    }

    private void validatePaymentStatusForCancel(Payment payment) {
        if (payment.getPaymentStatus() != PaymentStatus.NEW &&
                payment.getPaymentStatus() != PaymentStatus.READY_TO_CLEAR) {
            throw new DataValidationException("It is impossible to perform this operation with this payment, " +
                    "since it has already been closed");
        }
    }

    private void validateStatus(Payment payment, PaymentStatus paymentStatus) {
        if (!payment.getPaymentStatus().equals(paymentStatus)) {
            throw new DataValidationException(
                    String.format("Incorrect payment status, " +
                            "should be %s, but status is %s", paymentStatus, payment.getPaymentStatus()));
        }
    }

    private void validatePaymentStatusIsAlreadyCorrect(Payment payment, PaymentStatus status) {
        if (payment.getPaymentStatus().equals(status)) {
            throw new DataValidationException(
                    String.format("Payment status is already %s", payment.getPaymentStatus()));
        }
    }
}