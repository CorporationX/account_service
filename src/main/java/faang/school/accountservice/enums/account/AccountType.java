package faang.school.accountservice.enums.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountType {
    CHECKING("4200"),
    CREDIT("5200"),
    CORPORATE("8800"),
    DEPOSIT("5236"),
    RETIREMENT("2200");

    private final String value;
}