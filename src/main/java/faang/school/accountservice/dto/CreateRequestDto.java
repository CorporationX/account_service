package faang.school.accountservice.dto;

import faang.school.accountservice.enums.RequestType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class CreateRequestDto {
    @NotNull
    private UUID idempotencyKey;

    @Positive
    private Long userId;

    @NotNull
    private RequestType type;

    @NotNull
    private String lockKey;

    @NotNull
    private Map<String, Object> inputData;
}
