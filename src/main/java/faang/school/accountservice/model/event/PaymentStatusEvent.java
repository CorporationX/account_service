package faang.school.accountservice.model.event;

import faang.school.accountservice.model.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusEvent {
    private Long requestId;
    private UUID idempotencyToken;
    private RequestStatus status;
    private String statusDetails;
    private LocalDateTime paymentEventSentDateTime;
}
