package faang.school.accountservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountType {
    CHECKING_INDIVIDUAL("Checking Account (Individual)"),
    CHECKING_CORPORATE("Checking Account (Corporate)"),
    CURRENCY_ACCOUNT("Currency Account"),
    SAVINGS_ACCOUNT("Savings Account"),
    INVESTMENT_ACCOUNT("Investment Account");

    private final String description;
}
