package faang.school.accountservice.enums;

import lombok.Getter;

@Getter
public enum AccountType {

    INDIVIDUAL("4520"),
    LEGAL("4280"),
    CURRENCY("6540");

    private final String value;

    AccountType(String value) {
        this.value = value;
    }
}
