package faang.school.accountservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "DTO for response about account operation status")
public record AccountOperationResponse(

        @Schema(description = "Unique identifier of the operation",
                example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Current status of the operation",
                example = "SUCCESS")
        OperationStatus status,

        @Schema(description = "Type of the performed operation",
                example = "DEPOSIT")
        OperationType operationType,

        @Schema(description = "Additional message about operation result",
                example = "Operation completed successfully")
        String message
) {
}
