package faang.school.accountservice.enums;

public enum AccountType {
    /**
     * Расчетный счет для физических лиц
     */
    INDIVIDUAL_CHECKING,

    /**
     * Расчетный счет для юридических лиц
     */
    LEGAL_ENTITY_CHECKING,

    /**
     * Валютный счет
     */
    CURRENCY_ACCOUNT,

    /**
     * Сберегательный счет
     */
    SAVINGS_ACCOUNT,

    /**
     * Текущий счет
     */
    CURRENT_ACCOUNT
}
