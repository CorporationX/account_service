package faang.school.accountservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountType {
    CREDIT (4200_0000_0000_0000L),
    SETTLEMENT(8476_0000_0000_0000L),
    FIXED_TERM_SAVINGS(9245_0000_0000_0000L),
    SALARY(3468_0000_0000_0000L),
    CORPORATE(2528_0000_0000_0000L);

    private final long pattern;
}
