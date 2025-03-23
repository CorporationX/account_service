package faang.school.accountservice.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum RequestType {
    @JsonEnumDefaultValue
    UNKNOWN,
    TRANSFER
}
