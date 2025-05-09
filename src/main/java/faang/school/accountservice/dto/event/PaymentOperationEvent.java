package faang.school.accountservice.dto.event;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PaymentOperationEvent(
        UUID idempotencyToken,
        OperationType operationType,
        Long senderId,
        Long receiverId,
        OwnerType receiverType,
        BigDecimal amount,
        Currency currency,
        LocalDateTime clearScheduledAt
) {}
