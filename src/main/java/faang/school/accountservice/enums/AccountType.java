package faang.school.accountservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountType {
    CURRENCY_ACCOUNT(7538),
    INDIVIDUAL_ACCOUNT(4200),
    LEGAL_ENTITY_ACCOUNT(5236),

    ;

    private final long code;
}
