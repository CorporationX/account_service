package faang.school.accountservice.enums.auth_payment;

public enum PaymentProcessingResult {
    AUTHORIZED,
    CLEARED,
    CANCELLED,
    NOT_ENOUGH_FUNDS,
    PAYMENT_NOT_FOUND,
    PAYMENT_ALREADY_CANCELLED,
    PAYMENT_ALREADY_CLEARED,
    ERROR
}
