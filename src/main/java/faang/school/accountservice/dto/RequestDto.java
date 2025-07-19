package faang.school.accountservice.dto;

import faang.school.accountservice.enums.RequestStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RequestDto {
    private UUID idempotencyKey;
    private Long userId;
    private boolean isOpen = true;
    private RequestStatus requestStatus;
    private String statusDetails;
    private LocalDateTime updatedAt;

}
