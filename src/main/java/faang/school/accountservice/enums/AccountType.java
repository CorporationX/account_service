package faang.school.accountservice.enums;

import lombok.Getter;

@Getter
public enum AccountType {
    CURRENT_INDIVIDUALS("4200"),
    CURRENT_LEGAL("4300"),
    FOREIGN_CURRENCY("4400"),
    DEPOSIT("4500");

    private final String prefix;

    AccountType(String prefix) {
        this.prefix = prefix;
    }
}
