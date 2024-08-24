package faang.school.accountservice.enums;

import lombok.Data;
import lombok.Getter;

@Getter
public enum AccountType {
    CORPORATE(4200, 20),
    INDIVIDUAL(4400, 20),
    INVESTMENT(5400, 18),
    RETIREMENT(5600, 18);

    private final int prefix;
    private final int length;

    AccountType(int prefix, int length) {
        this.prefix = prefix;
        this.length = length;
    }
}
