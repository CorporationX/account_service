package faang.school.accountservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStages {
    PENDING("В ожидании"),
    AUTHORIZED("Авторизован"),
    CLEARED("Проведен"),
    CANCELED("Отменен"),
    FAILED("Неудачный");

    private final String description;
}