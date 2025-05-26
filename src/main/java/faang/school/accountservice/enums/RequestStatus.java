package faang.school.accountservice.enums;

import lombok.Getter;

@Getter
public enum RequestStatus {

    TO_BE_COMPLETED("К выполнению"),
    PENDING_EXECUTION("В ожидании выполнения"),
    DONE("Выполнено"),
    CANCELED("Отменено");

    private final String description;

    RequestStatus(String description) {
        this.description = description;
    }
}
