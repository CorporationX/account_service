package faang.school.accountservice.dto.message;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ClearingMessage {
    @NotNull
    private UUID operationId;

    @NotNull
    private UUID authorizationId;

    @NotNull
    private Instant timestamp;
}
