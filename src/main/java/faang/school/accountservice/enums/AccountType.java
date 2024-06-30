package faang.school.accountservice.enums;

import lombok.Getter;

@Getter
public enum AccountType {
    CURRENT_INDIVIDUALS(4200_0000_0000_0000L),
    CURRENT_LEGAL(4300_0000_0000_0000L),
    FOREIGN_CURRENCY(4400_0000_0000_0000L),
    DEPOSIT(4500_0000_0000_0000L);

    private final long pattern;

    AccountType(long pattern) {
        this.pattern = pattern;
    }
}
