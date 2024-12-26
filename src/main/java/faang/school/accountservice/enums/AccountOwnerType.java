package faang.school.accountservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;


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
        throw new RuntimeException (String.format("Invalid account owner type: %s", json));
    }
}
