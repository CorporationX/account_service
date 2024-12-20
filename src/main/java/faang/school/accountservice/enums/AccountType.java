package faang.school.accountservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import faang.school.accountservice.exception.IllegalAccountTypeException;

public enum AccountType {
    CURRENT,
    SAVINGS,
    DEPOSIT,
    INVESTMENT,
    CREDIT,
    CURRENCY;

    @JsonCreator
    public static AccountType toValue(String json) {
        for (AccountType value : AccountType.values()) {
            if (value.name().equalsIgnoreCase(json)) {
                return value;
            }
        }
        throw new IllegalAccountTypeException(String.format("Invalid account type: %s", json));
    }
}
