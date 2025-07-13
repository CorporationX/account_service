package faang.school.accountservice.event.payment;

import java.time.LocalDateTime;
import java.util.UUID;

public record FailedPaymentAuthorizationEventDto (UUID operationId, LocalDateTime timestamp) {
}