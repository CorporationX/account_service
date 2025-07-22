package faang.school.accountservice.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RequestEvent {
    private UUID idempotencyKey;
    private Long userId;
    private String requestType;
    private String currentStatus;
    private String statusDetails;
    private String eventType;
    private LocalDateTime timestamp;
}
