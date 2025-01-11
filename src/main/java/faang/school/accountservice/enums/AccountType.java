package faang.school.accountservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountType {
    DEBIT( 4200),
    SAVING( 5236);

    private final Integer prefix;
}