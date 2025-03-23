package faang.school.accountservice.dto.request;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class RequestGetDto {
    private UUID idempotencyKey;
    private Long userId;
    private RequestType requestType;
    private Map<String, Object> inputParams;
    private RequestStatus requestStatus;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
