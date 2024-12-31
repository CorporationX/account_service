package faang.school.accountservice.validator.payment;

import faang.school.accountservice.exception.payment.InsufficientFundsException;
import faang.school.accountservice.exception.payment.InvalidPaymentStatusException;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.payment.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

import static faang.school.accountservice.model.payment.PaymentStatus.ACTIVE;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentValidator {

    public void checkFreeAmount(UUID operationId, Balance sourceBalance, BigDecimal amount) {
        log.debug("Validating balance for operationId={}, balanceId={}, requestedAmount={}",
                operationId, sourceBalance.getId(), amount);

        if (sourceBalance.getActualBalance().compareTo(amount) < 0) {
            log.warn("Insufficient funds: operationId={}, balanceId={}, actualBalance={}, requestedAmount={}",
                    operationId, sourceBalance.getId(), sourceBalance.getActualBalance(), amount);
            throw new InsufficientFundsException(
                    "Insufficient funds, operationId=%s, balanceId=%s"
                            .formatted(operationId, sourceBalance.getId())
            );
        }
        log.debug("Sufficient funds for operationId={}, balanceId={}", operationId, sourceBalance.getId());
    }

    public void checkAuthPaymentStatus(Payment payment) {
        log.debug("Checking payment status for operation paymentId={}, currentStatus={}"
                , payment.getId(), payment.getStatus());

        if (!ACTIVE.equals(payment.getStatus())) {
            log.warn("Operation is not possible, paymentId={}, currentStatus={}",
                    payment.getId(), payment.getStatus());
            throw new InvalidPaymentStatusException(
                    "Payment with id=%s cannot be accepted/rejected, current status=%s"
                            .formatted(payment.getId(), payment.getStatus())
            );
        }

        log.debug("Payment is ACTIVE, operation is possible, paymentId={}", payment.getId());
    }
}