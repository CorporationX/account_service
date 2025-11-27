package faang.school.accountservice.enums.request;

public enum RequestStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    CANCELLED,
    FAILED;

    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }
}
