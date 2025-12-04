package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record RequestUpdateContextDto(
        @NotEmpty
        Map<String, Object> inputRequest
) {
}
