package faang.school.accountservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountType {
    DEBIT( 4200),
    SAVINGS( 5236),
    CURRENT ( 5234),
    SPECIAL( 5233),
    CURRENCY( 5232),
    BUSINESS( 5231),
    INDIVIDUAL( 5216),
    LEGAL( 5206);


    private final Integer prefix;
}