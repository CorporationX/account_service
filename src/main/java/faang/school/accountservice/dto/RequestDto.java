package faang.school.accountservice.dto;

import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.enums.RequestStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RequestDto {

    private UUID idempotencyToken;
    private Long userId;
    private OperationType operationType;
    private RequestInput requestInput;
    private Long lockValue;
    private Boolean isOpen;
    private RequestStatus requestStatus;
    private String statusDetails;
}
