package faang.school.accountservice.enums.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountType {
    SAVINGS("5536"),
    DEBIT("4276"),
    CREDIT("5200");

    private final String code;
}