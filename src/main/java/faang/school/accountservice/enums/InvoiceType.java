package faang.school.accountservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InvoiceType {
    DEBIT(4200),
    CREDIT(5400),
    SAVINGS(5236),
    MORTGAGE(6000),
    CORPORATE(8000),
    TRADING(9000);

    private final int prefixCode;
}
