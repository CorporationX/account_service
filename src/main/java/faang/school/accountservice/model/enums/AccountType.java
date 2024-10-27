package faang.school.accountservice.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountType {
    INDIVIDUAL(4200, 20),
    CREDIT_OVERDUE(4000, 20),
    DEBIT_OVERDUE(4100, 20);

    private final int mask;
    private final int length;
}
