package faang.school.accountservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountNumberType {
    DEBIT("4200"),
    SAVINGS("4210"),
    CREDIT("4220"),
    LOAN("4230"),
    INVESTMENT("4240"),
    CHECKING("4250"),
    BUSINESS("4260"),
    RETIREMENT("4270"),
    FOREX("4280"),
    ESCROW("4290");

    private final String code;
}