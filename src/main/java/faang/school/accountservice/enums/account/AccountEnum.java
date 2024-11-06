package faang.school.accountservice.enums.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountEnum {
    DEBIT("4200"),
    SAVINGS("5236"),
    CREDIT("4477"),
    DEPOSIT("2222"),
    SETTLEMENT("9015"),
    SUBSETTLEMENT("9055"),
    LETTER_OF_CREDIT("1240"),
    BUDGET("3388");

    private final String prefix;
}