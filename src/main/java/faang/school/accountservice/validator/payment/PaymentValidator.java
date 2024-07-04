package faang.school.accountservice.validator.payment;

import faang.school.accountservice.dto.payment.PaymentDtoToCreate;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.Payment;
import faang.school.accountservice.model.enums.PaymentStatus;
import faang.school.accountservice.repository.BalanceRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentValidator {
    private final BalanceRepository balanceRepository;

    public void validateUserIsBalanceOwner(Long balanceId, Long userId) {
        try {
            Boolean isOwner = balanceRepository.isBalanceOwner(balanceId, userId);
            if (!isOwner) {
                throw new DataValidationException(
                        String.format("Only balance owner could send money from balance {}", balanceId));
            }
        } catch (FeignException e) {
            throw e;
        }
    }

    public void validateSenderHaveEnoughMoneyOnBalance(Balance senderBalance, Payment payment) {
        if (senderBalance.getAuthorizationBalance().compareTo(payment.getAmount()) < 0) {
            throw new DataValidationException("Not enough money");
        }
    }

    public void validatePaymentStatusForCancel(Payment payment) {
        if (payment.getPaymentStatus() != PaymentStatus.NEW &&
                payment.getPaymentStatus() != PaymentStatus.READY_TO_CLEAR) {
            throw new DataValidationException("It is impossible to perform this operation with this payment, " +
                    "since it has already been closed");
        }
    }

    public void validateAuthorizeStatus(Payment payment) {
        if(!payment.getPaymentStatus().equals(PaymentStatus.NEW)) {
            throw new DataValidationException(
                    String.format("Incorrect payment status {}", payment.getPaymentStatus()));
        }
    }

    public void validateStatus(Payment payment, PaymentStatus paymentStatus) {
        if(!payment.getPaymentStatus().equals(paymentStatus)) {
            throw new DataValidationException(
                    String.format("Incorrect payment status, " +
                            "should be {}, but status is {}", paymentStatus, payment.getPaymentStatus()));
        }
    }
}