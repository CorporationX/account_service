package faang.school.accountservice.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    SUCCESS("Payment successful"),
    INSUFFICIENT_BALANCE("Insufficient balance"),
    USER_LIMIT_EXCEEDED("User transaction limit exceeded"),
    BANK_LIMIT_EXCEEDED("Bank transaction limit exceeded");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

}
