package faang.school.accountservice.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum RequestStatus {
    @JsonEnumDefaultValue
    UNKNOWN,
    TODO,
    COMPLETED,
    CANCELLED
}
