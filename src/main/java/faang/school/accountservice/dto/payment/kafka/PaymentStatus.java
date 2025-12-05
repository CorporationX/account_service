package faang.school.accountservice.dto.payment.kafka;

public enum PaymentStatus {
    ON_AUTHORIZATION,
    AUTHORIZATION_SUCCESS,
    AUTHORIZATION_FAIL,
    ON_CLEARING,
    CLEARING_SUCCESS,
    CLEARING_FAIL,
    ON_CANCELLING,
    CANCEL_SUCCESS,
    CANCEL_FAIL,
    SERVER_ERROR
}
