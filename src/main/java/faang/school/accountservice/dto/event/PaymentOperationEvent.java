package faang.school.accountservice.dto.event;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentOperationEvent(
        OperationType operationType,
        Long senderId,
        OwnerType senderType,
        Long receiverId,
        OwnerType receiverType,
        BigDecimal amount,
        Currency currency,
        LocalDateTime clearScheduledAt
) {}
