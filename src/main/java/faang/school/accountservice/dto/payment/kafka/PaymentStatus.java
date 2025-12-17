package faang.school.accountservice.dto.payment.kafka;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    ON_AUTHORIZATION("On Authorization"),
    AUTHORIZATION_SUCCESS("Successful authorization"),
    AUTHORIZATION_FAIL("Authorization failed"),
    ON_CLEARING("On Clearing"),
    CLEARING_SUCCESS("Successful clearing"),
    CLEARING_FAIL("Clearing failed"),
    ON_CANCELLING("On Cancelling"),
    CANCEL_SUCCESS("Successful cancel"),
    CANCEL_FAIL("Cancel failed"),
    SERVER_ERROR("Server internal error");

    private String description;

    PaymentStatus(String description) {
        this.description = description;
    }
}
