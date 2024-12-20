package faang.school.accountservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import faang.school.accountservice.exception.IllegalAccountOwnerTypeException;

public enum AccountOwnerType {
    USER,
    PROJECT;

    @JsonCreator
    public static AccountOwnerType toValue(String json) {
        for (AccountOwnerType value : AccountOwnerType.values()) {
            if (value.name().equalsIgnoreCase(json)) {
                return value;
            }
        }
        throw new IllegalAccountOwnerTypeException(String.format("Invalid account owner type: %s", json));
    }
}