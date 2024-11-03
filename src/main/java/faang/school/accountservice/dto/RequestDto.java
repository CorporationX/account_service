package faang.school.accountservice.dto;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.OperationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {

    private UUID id;

    @NotNull(message = "createdBy cannot be null")
    private Long createdBy;

    @NotNull(message = "type cannot be null")
    private OperationType type;

    private Map<String, Object> inputData;
    private RequestStatus status;
    private String statusDescription;
}

