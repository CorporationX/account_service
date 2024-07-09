package faang.school.accountservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountType {
    CURRENT_ACCOUNT(4200),
    SAVINGS_ACCOUNT(5200),
    INVESTMENT_ACCOUNT(6200),
    PERSONAL_ACCOUNT(7200),
    CREDIT_ACCOUNT(8200);

    private final int code;
}
