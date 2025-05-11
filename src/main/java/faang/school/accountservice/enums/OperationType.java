package faang.school.accountservice.enums;

import lombok.Getter;

@Getter
public enum OperationType {
    MONEY_TRANSFER("Перевод средств"),
    PAYMENT("Платеж"),
    WITHDRAWAL("Снятие средств"),
    DEPOSIT("Пополнение счета"),

    USER_REGISTRATION("Регистрация пользователя"),
    USER_UPDATE("Обновление данных пользователя"),
    PASSWORD_CHANGE("Смена пароля"),

    ORDER_CREATION("Создание заказа"),
    ORDER_CANCELLATION("Отмена заказа"),
    ORDER_REFUND("Возврат заказа");

    private final String description;

    OperationType(String description) {
        this.description = description;
    }
}
