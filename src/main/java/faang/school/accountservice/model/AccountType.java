package faang.school.accountservice.model;

public enum AccountType {
    CURRENT,           // Расчетный счет (основной для операций)
    SAVINGS,           // Сберегательный (накопления)
    FOREIGN_CURRENCY,  // Валютный (для USD, EUR etc.)
    CORPORATE,         // Корпоративный (для юр.лиц)
    INDIVIDUAL,        // Индивидуальный (для физ.лиц/ИП)
    CREDIT,            // Кредитный
    DEBIT,             // Дебетовый
    BROKERAGE          // Брокерский (для ценных бумаг)
}
