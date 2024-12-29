package faang.school.accountservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import faang.school.accountservice.exception.InvalidTransactionTypeException;

import java.util.Arrays;

public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL;

    @JsonCreator
    public static TransactionType toValue(String json) {
        for (TransactionType value : TransactionType.values()) {
            if (value.name().equalsIgnoreCase(json)) {
                return value;
            }
        }

        throw new InvalidTransactionTypeException(String.format(
                "Invalid transaction type: %s. Valid values are: %s", json, Arrays.toString(TransactionType.values())));
    }
}