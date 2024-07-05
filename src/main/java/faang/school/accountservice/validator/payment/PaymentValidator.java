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

    public void validateUserIsBalanceOwner(Long balanceId, Long userId) {
        if (!balanceRepository.isBalanceOwner(balanceId, userId)) {
            throw new DataValidationException(
                    String.format("Only balance owner could send money from balance %s", balanceId));
        }
    }

    public void validateSenderHaveEnoughMoneyOnAuthorizationBalance(Balance senderBalance, Payment payment) {
        if (senderBalance.getAuthorizationBalance().compareTo(payment.getAmount()) < 0) {
            throw new DataValidationException("Not enough money on authorization balance");
        }
    }

    public void validateSenderHaveEnoughMoneyOnActualBalance(Balance senderBalance, Payment payment) {
        if (senderBalance.getAuthorizationBalance().compareTo(payment.getAmount()) < 0) {
            throw new DataValidationException("Not enough money on actual balance");
        }
    }

    public void validatePaymentStatusForCancel(Payment payment) {
        if (payment.getPaymentStatus() != PaymentStatus.NEW &&
                payment.getPaymentStatus() != PaymentStatus.READY_TO_CLEAR) {
            throw new DataValidationException("It is impossible to perform this operation with this payment, " +
                    "since it has already been closed");
        }
    }

    public void validateStatus(Payment payment, PaymentStatus paymentStatus) {
        if (!payment.getPaymentStatus().equals(paymentStatus)) {
            throw new DataValidationException(
                    String.format("Incorrect payment status, " +
                            "should be %s, but status is %s", paymentStatus, payment.getPaymentStatus()));
        }
    }

    public void validatePaymentStatusIsAlreadyCorrect(Payment payment, PaymentStatus status) {
        if (payment.getPaymentStatus().equals(status)) {
            throw new DataValidationException(
                    String.format("Payment status is already %s", payment.getPaymentStatus()));
        }
    }
}