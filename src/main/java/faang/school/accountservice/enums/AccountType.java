package faang.school.accountservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountType {
    CHECKING_INDIVIDUAL(4200, "checking_individual_seq"),
    CHECKING_CORPORATE(4210, "checking_corporate_seq"),
    CURRENCY_ACCOUNT(4300, "currency_account_seq"),
    SAVINGS_ACCOUNT(5236, "savings_account_seq"),
    INVESTMENT_ACCOUNT(5400, "investment_account_seq");

    private final int number;
    private final String sequenceName;
}
