package faang.school.accountservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountType {
    PERSONAL_CURRENT("Расчетный счет для физ. лиц"),
    BUSINESS_CURRENT("Расчетный счет для юр. лиц"),
    CURRENCY("Валютный счет"),
    SAVINGS("Накопительный счет")
    ;

    private final String description;
}
