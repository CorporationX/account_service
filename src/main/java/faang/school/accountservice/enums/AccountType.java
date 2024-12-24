package faang.school.accountservice.enums;

import lombok.Getter;

public enum AccountType {
    PERSONAL("4200"),
    BUSINESS("5236"),
    CURRENCY("6300"),
    SAVINGS("5312");

    @Getter
    private final String prefix;

    AccountType(String prefix) {
        this.prefix = prefix;
    }
}
