package faang.school.accountservice.model.account;

import lombok.Getter;

@Getter
public enum AccountType {
    DEBIT,
    CURRENCY,
    CREDIT,
    DEPOSIT,
    SAVINGS_ACCOUNT
}