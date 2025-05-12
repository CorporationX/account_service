package faang.school.accountservice.dto.event;

import faang.school.accountservice.enums.OperationType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ResponseOutboxEvent(
        UUID idempotencyToken,
        OperationType operationType
) {}
