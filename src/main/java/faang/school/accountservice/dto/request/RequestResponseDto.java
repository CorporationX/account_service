package faang.school.accountservice.dto.request;

import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.enums.request.RequestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestResponseDto {
    private UUID idempotencyToken;
    private Long userId;
    private RequestType requestType;
    private Map<String, Object> storage;
    private RequestStatus status;
    private String details;
}
