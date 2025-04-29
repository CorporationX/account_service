package faang.school.accountservice.enums;

import lombok.Getter;

public enum AccountType {
INDIVIDUAL(1000),
    LEGAL(2000),
    SAVINGS(5236),
    DEBIT(4200),
    CREDIT(3000);

    @Getter
    private final int code;

    AccountType(int code) {
        this.code = code;
    }
}
