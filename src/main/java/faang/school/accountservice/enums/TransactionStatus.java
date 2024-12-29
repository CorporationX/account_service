package faang.school.accountservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import faang.school.accountservice.exception.InvalidTransactionTypeException;

import java.util.Arrays;

public enum TransactionStatus {
    PENDING,
    APPROVED,
    REJECTED;

    @JsonCreator
    public static TransactionStatus toValue(String json) {
        for (TransactionStatus value : TransactionStatus.values()) {
            if (value.name().equalsIgnoreCase(json)) {
                return value;
            }
        }

        throw new InvalidTransactionTypeException(String.format(
                "Invalid transaction status: %s. Valid values are: %s", json, Arrays.toString(TransactionType.values())));
    }
}