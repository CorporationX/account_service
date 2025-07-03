package faang.school.accountservice.dto;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseRequestDto {
    private UUID idempotencyToken;
    private Long userId;
    private RequestType requestType;
    private String valueLock;
    private Boolean isOpen;
    private Map<String, String> requestInputData;
    private RequestStatus status;
    private String statusDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}
