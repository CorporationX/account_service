package faang.school.accountservice.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountType {
    BUDGET_ACCOUNT(0)/*("Budget account")*/,
    CREDIT_ACCOUNT(1)/*("Credit account")*/,
    CURRENCY_ACCOUNT(2)/*("Currency account")*/,
    SETTLEMENT_ACCOUNT(3)/*("Settlement account")*/;

    private final int value;
}
