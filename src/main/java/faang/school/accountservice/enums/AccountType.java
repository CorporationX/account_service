package faang.school.accountservice.enums;

public enum AccountType {
    PERSONAL_CHECKING("Расчетный счет для физ. лиц"),
    BUSINESS_CHECKING("Расчетный счет для юр. лиц"),
    SAVINGS("Сберегательный счет"),
    CURRENCY("Валютный счет"),
    DEPOSIT("Депозитный счет");

    private final String description;

    AccountType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}