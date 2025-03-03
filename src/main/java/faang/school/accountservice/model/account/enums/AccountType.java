package faang.school.accountservice.model.account.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountType {
    CURRENT_INDIVIDUAL("Расчетный счет для физического лица"),
    CURRENT_BUSINESS("Расчетный счет для юридического лица"),
    SAVINGS("Сберегательный счет"),
    CURRENCY("Валютный счет"),
    INVESTMENT("Инвестиционный счет"),
    CREDIT("Кредитный счет"),
    DEPOSIT("Депозитный счет"),
    ESCROW("Эскроу-счет"),
    JOINT("Совместный счет"),
    TRUST("Доверительный счет");

    private final String description;
}
