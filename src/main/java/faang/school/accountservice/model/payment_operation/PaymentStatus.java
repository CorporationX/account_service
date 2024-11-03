package faang.school.accountservice.model.payment_operation;

public enum PaymentStatus {
    AUTH_PENDING,
    AUTH_ERROR,
    AUTH_SUCCESS,

    SCHEDULED_PENDING,
    SCHEDULED_SUCCESS,

    CANCEL_PENDING,
    CANCEL_SUCCESS,

    FORCED_PENDING,
    FORCED_SUCCESS
}
