package faang.school.accountservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import faang.school.accountservice.exception.IllegalAccountStatusException;

public enum AccountStatus {
    ACTIVE,
    INACTIVE,
    FROZEN,
    DELETED;

    @JsonCreator
    public static AccountStatus toValue(String json) {
        for (AccountStatus value : AccountStatus.values()) {
            if (value.name().equalsIgnoreCase(json)) {
                return value;
            }
        }
        throw new IllegalAccountStatusException(String.format("Invalid account status: %s", json));
    }
}