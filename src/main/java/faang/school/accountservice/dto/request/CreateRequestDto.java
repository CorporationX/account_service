package faang.school.accountservice.dto.request;

import faang.school.accountservice.enums.request.OperationType;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record CreateRequestDto(
        Long userId,
        Long projectId,

        @NotNull
        OperationType operationType,

        @NotNull
        String lockValue,

        @NotNull
        Map<String, Object> inputData
) {
}
