package faang.school.accountservice.enums;

import lombok.Getter;

@Getter
public enum AccountType {
    FL(40817),
    UL(40701),
    FL_DEPOSIT(42301),
    UL_DEPOSIT(42001);

    private final int accountTypeNumber;

    AccountType(int accountTypeNumber) {
        this.accountTypeNumber = accountTypeNumber;
    }
}
