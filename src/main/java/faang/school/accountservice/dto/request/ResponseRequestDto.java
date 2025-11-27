package faang.school.accountservice.dto.request;

import faang.school.accountservice.enums.request.OperationType;
import faang.school.accountservice.enums.request.RequestStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record ResponseRequestDto(
        UUID idempotencyToken,
        Long userId,
        Long projectId,
        OperationType operationType,
        String lockValue,
        Map<String, Object> inputData,
        RequestStatus requestStatus,
        String statusDetails,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
