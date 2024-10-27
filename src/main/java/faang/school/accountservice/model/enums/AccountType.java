package faang.school.accountservice.model.enums;

import lombok.Getter;

@Getter
public enum AccountType {
    INDIVIDUAL(4200, 20),
    CREDIT_OVERDUE(4000, 20),
    DEBIT_OVERDUE(4100, 20);

    private final int mask;
    private final int length;

    AccountType(int mask, int length) {
        this.mask = mask;
        this.length = length;
    }
}
