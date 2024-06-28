package faang.school.accountservice.enums;

import lombok.Getter;

@Getter
public enum AccountType {
    SAVINGS(5236_0000_0000_0000L),
    DEBIT(4200_0000_0000_0000L);

    private final Long pattern;

    AccountType(long pattern) {
        this.pattern = pattern;
    }
}
