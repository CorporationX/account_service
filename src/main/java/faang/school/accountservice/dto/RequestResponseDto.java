package faang.school.accountservice.dto;

import faang.school.accountservice.model.RequestStatus;
import faang.school.accountservice.model.RequestType;
import java.util.Map;
import java.util.UUID;

public record RequestResponseDto(
        UUID idempotencyKey,
        long userId,
        RequestType requestType,
        RequestStatus requestStatus,
        boolean isOpen,
        Map<String, Object> inputRequest,
        String statusDetails
) {
}
