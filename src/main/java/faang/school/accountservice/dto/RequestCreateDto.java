package faang.school.accountservice.dto;

import faang.school.accountservice.model.RequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record RequestCreateDto(
        @NotNull
        long userId,
        @NotNull
        RequestType requestType,
        @NotBlank
        String lockKey,
        @NotEmpty
        Map<String, Object> inputRequest
) {
}
