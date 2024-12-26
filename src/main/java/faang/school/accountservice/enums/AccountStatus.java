package faang.school.accountservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import faang.school.accountservice.exception.IllegalAccountStatusException;

import java.util.Arrays;

public enum AccountStatus {
    ACTIVE,
    INACTIVE,
    FROZEN;

    @JsonCreator
    public static AccountStatus toValue(String json) {
        for (AccountStatus value : AccountStatus.values()) {
            if (value.name().equalsIgnoreCase(json)) {
                return value;
            }
        }

        throw new IllegalAccountStatusException(String.format(
                "Invalid account status: %s. Valid values are: %s", json, Arrays.toString(AccountStatus.values())));
    }
}