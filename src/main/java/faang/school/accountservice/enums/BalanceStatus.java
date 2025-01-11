package faang.school.accountservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum BalanceStatus {
    NEW,
    ACTIVE,
    BLOCKED,
    CLOSED;

    @JsonCreator
    public static BalanceStatus toValue(String json) {
        for (BalanceStatus value : BalanceStatus.values()) {
            if (value.name().equalsIgnoreCase(json)) {
                return value;
            }
        }

        throw new IllegalArgumentException(String.format(
                "Invalid balance status: %s. Valid values are: %s", json, Arrays.toString(BalanceStatus.values())));
    }
}
