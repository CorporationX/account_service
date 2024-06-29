package faang.school.accountservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountType {
    SAVINGS(5236_0000_0000_0000L),
    DEBIT(4200_0000_0000_0000L),
    RUBLE_ACCOUNT_FOR_INDIVIDUALS(2500_0000_0000_0000L),
    RUBLE_ACCOUNT_FOR_LEGAL(8400_0000_0000_0000L),
    CURRENCY_ACCOUNT_FOR_INDIVIDUALS(9900_0000_0000_0000L),
    CURRENCY_ACCOUNT_FOR_LEGAL(6700_0000_0000_0000L);

    private final Long pattern;
}
