package faang.school.accountservice.dto;

import faang.school.accountservice.model.RequestStatus;
import faang.school.accountservice.model.RequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

public record RequestResponseDto(
        @NotNull
        UUID idempotencyKey,
        @NotNull
        long userId,
        @NotNull
        RequestType requestType,
        @NotNull
        RequestStatus requestStatus,
        boolean isOpen,
        @NotNull
        Map<String, Object> inputRequest,
        @NotBlank
        String statusDetails
) {
}
