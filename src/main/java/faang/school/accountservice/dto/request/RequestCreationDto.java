package faang.school.accountservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestCreationDto {
    @NotNull(message = "Idempotency token can't be null")
    private UUID idempotencyToken;

    @NotNull(message = "User ID can't be null")
    private Long userId;

    @NotNull(message = "Request type can't be null")
    private RequestTypeDto type;
    private Map<String, Object> inputData;
}
