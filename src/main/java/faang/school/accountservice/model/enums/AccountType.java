package faang.school.accountservice.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountType {
    BUDGET_ACCOUNT(0),
    CREDIT_ACCOUNT(1),
    CURRENCY_ACCOUNT(2),
    SETTLEMENT_ACCOUNT(3);

    private final int value;
}
