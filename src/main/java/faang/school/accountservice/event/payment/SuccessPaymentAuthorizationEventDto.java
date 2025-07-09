package faang.school.accountservice.event.payment;

import java.util.UUID;

public record SuccessPaymentAuthorizationEventDto (UUID operationToken) {
}
