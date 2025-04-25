package faang.school.accountservice.dto;

import faang.school.accountservice.enums.RequestType;
import lombok.NonNull;

import java.util.Map;
import java.util.UUID;

public record CreateRequestDto(
        @NonNull
        UUID token,
        long userId,
        @NonNull
        RequestType requestType,
        long blockValue,
        @NonNull
        Map<String, Object> body,
        String details
) {
}
