package faang.school.accountservice.dto;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record RequestDto (

    UUID id,

    @NotNull(message = "createdBy cannot be null")
    Long createdBy,

    @NotNull(message = "type cannot be null")
    RequestType type,

    Map<String, Object> inputData,

    @NotNull(message = "requestStatus cannot be null")
    RequestStatus requestStatus,

    @NotNull(message = "lockValue cannot be null")
    Long lockValue,

    String statusDescription
    ){
}

