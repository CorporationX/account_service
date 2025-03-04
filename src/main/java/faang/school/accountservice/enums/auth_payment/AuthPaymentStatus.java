package faang.school.accountservice.enums.auth_payment;

import java.util.Optional;

public enum AuthPaymentStatus {
    AUTHORIZED(null),
    CANCELLED(PaymentProcessingResult.PAYMENT_ALREADY_CANCELLED),
    CLEARED(PaymentProcessingResult.PAYMENT_ALREADY_CLEARED),
    ERROR(PaymentProcessingResult.ERROR);

    private final PaymentProcessingResult result;

    AuthPaymentStatus(PaymentProcessingResult result) {
        this.result = result;
    }

    public Optional<PaymentProcessingResult> getProcessingResult() {
        return Optional.ofNullable(result);
    }
}

