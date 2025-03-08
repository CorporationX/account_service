package faang.school.accountservice.entity.cashback;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TransactionType {
    TRANSFER, SUPPORTING;

    @JsonCreator
    public static TransactionType fromString(String value) {
        return TransactionType.valueOf(value.toUpperCase());
    }
}
