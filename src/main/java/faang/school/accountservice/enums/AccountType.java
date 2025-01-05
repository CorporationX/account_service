package faang.school.accountservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountType {
    DEBIT("DEBIT", 4200),
    SAVING("SAVING", 5236);

    private final String key;
    private final Integer value;
}