package faang.school.accountservice.enums;

import lombok.Getter;

@Getter
public enum AccountType {
    CORPORATE(4200, 18),
    INDIVIDUAL(4400, 18),
    INVESTMENT(5400, 16),
    RETIREMENT(5600, 16);

    private final int prefix;
    private final int length;

    AccountType(int prefix, int length) {
        this.prefix = prefix;
        this.length = length;
    }
}
