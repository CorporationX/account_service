package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record NotificationMessageDto(
        @NotNull
        Long userId,
        @NotNull
        UUID requestId,
        @NotBlank
        String message
) {
}
