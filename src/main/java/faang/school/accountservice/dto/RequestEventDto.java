package faang.school.accountservice.dto;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record RequestEventDto(
        @NonNull
        UUID id,
        long userId,
        @NonNull
        RequestType requestType,
        long blockValue,
        @NonNull
        Map<String, Object> body,
        RequestStatus requestStatus,
        String details,
        LocalDateTime timestamp
) {
}
