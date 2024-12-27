package faang.school.accountservice.validator.payment;

import faang.school.accountservice.exception.payment.InsufficientFundsException;
import faang.school.accountservice.exception.payment.InvalidAuthPaymentStatusException;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.payment.AuthPayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

import static faang.school.accountservice.model.payment.AuthPaymentStatus.ACTIVE;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthPaymentValidator {

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

    public void checkAuthPaymentStatus(AuthPayment payment, String operationName) {
        log.debug("Checking payment status for operation='{}', paymentId={}, currentStatus={}",
                operationName, payment.getId(), payment.getStatus());

        if (!ACTIVE.equals(payment.getStatus())) {
            log.warn("Operation '{}' is not possible, paymentId={}, currentStatus={}",
                    operationName, payment.getId(), payment.getStatus());
            throw new InvalidAuthPaymentStatusException(
                    "AuthPayment with id=%s cannot be %s, current status=%s"
                            .formatted(payment.getId(), operationName, payment.getStatus())
            );
        }

        log.debug("AuthPayment is ACTIVE, operation='{}' is possible, paymentId={}",
                operationName, payment.getId());
    }
}
