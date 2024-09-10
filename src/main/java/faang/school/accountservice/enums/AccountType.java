package faang.school.accountservice.enums;

import lombok.Getter;

@Getter
public enum AccountType {

    INDIVIDUAL(4200),
    COMPANY(4280);

    private final int prefix;

    AccountType(int prefix) {
        this.prefix = prefix;
    }
}