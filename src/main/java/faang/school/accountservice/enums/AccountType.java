package faang.school.accountservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum AccountType {
    SAVINGS(5236_0000_0000_0000L),
    DEBIT(4200_0000_0000_0000L),
    RUBLE_ACCOUNT_FOR_INDIVIDUALS,
    RUBLE_ACCOUNT_FOR_LEGAL,
    CURRENCY_ACCOUNT_FOR_INDIVIDUALS,
    CURRENCY_ACCOUNT_FOR_LEGAL


    private final Long pattern;

    AccountType(long pattern) {
        this.pattern = pattern;

    }
}
