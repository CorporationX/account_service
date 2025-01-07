package faang.school.accountservice.enums;

import lombok.Getter;

@Getter
public enum AccountType {
    CURRENT(1571_000_000_000_000L),
    SAVINGS(3312_000_000_000_000L),
    DEPOSIT(4412_000_000_000_000L),
    INVESTMENT(3711_000_000_000_000L),
    CREDIT(5236_000_000_000_000L),
    CURRENCY(7243_000_000_000_000L),
    DEBIT(4200_000_000_000_000L);

    private final long accountTypePattern;

    AccountType(long pattern) {
        this.accountTypePattern = pattern;
    }
}
