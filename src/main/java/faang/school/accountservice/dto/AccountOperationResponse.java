package faang.school.accountservice.dto;

import java.util.UUID;

public record AccountOperationResponse(
        UUID id,
        OperationStatus status,
        OperationType operationType,
        String message
) {
}
