package faang.school.accountservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountType {
    CHECKING_INDIVIDUAL(4200),
    CHECKING_CORPORATE(4210),
    CURRENCY_ACCOUNT(4300),
    SAVINGS_ACCOUNT(5236),
    INVESTMENT_ACCOUNT(5400);

    private final Integer code;
}
