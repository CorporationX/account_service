package faang.school.accountservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import faang.school.accountservice.exception.IllegalAccountTypeException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AccountType {
    CURRENT(1000_000_000_000_000L),
    SAVINGS(2000_000_000_000_000L),
    DEPOSIT(3000_000_000_000_000L),
    INVESTMENT(4000_000_000_000_000L),
    CREDIT(5000_000_000_000_000L),
    CURRENCY(6000_000_000_000_000L),
    DEBIT(7000_000_000_000_000L);

    private final long accountTypePattern;

    AccountType(long pattern) {
        this.accountTypePattern = pattern;
    }

    @JsonCreator
    public static AccountType toValue(String json) {
        for (AccountType value : AccountType.values()) {
            if (value.name().equalsIgnoreCase(json)) {
                return value;
            }
        }

        throw new IllegalAccountTypeException(String.format(
                "Invalid account type: %s. Valid values are: %s", json, Arrays.toString(AccountType.values())));
    }
}
