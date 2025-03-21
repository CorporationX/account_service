package faang.school.accountservice.enums;

import lombok.Getter;

@Getter
public enum AccountType {

    INDIVIDUAL("4525"),
    LEGAL("4288"),
    CURRENCY("6544");

    private final String value;

    AccountType(String value) {
        this.value = value;
    }
}
