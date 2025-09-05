package faang.school.accountservice.model.dto;

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
public class RequestDto {
    private UUID id;
    private Long userId;
    private String requestType;
    private String lockValue;
    private boolean isOpen;
    private Map<String,Object> inputData;
    private String status;
    private String statusDetails;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}