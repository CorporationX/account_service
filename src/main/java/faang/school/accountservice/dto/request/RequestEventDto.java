package faang.school.accountservice.dto.request;

import faang.school.accountservice.enums.request.OperationType;
import faang.school.accountservice.enums.request.RequestStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record RequestEventDto(
        UUID requestId,
        Long userId,
        OperationType operationType,
        RequestStatus status,
        LocalDateTime timestamp
) {
}