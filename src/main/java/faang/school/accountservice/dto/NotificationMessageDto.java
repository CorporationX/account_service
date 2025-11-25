package faang.school.accountservice.dto;

import java.util.UUID;

public record NotificationMessageDto(
        Long userId,
        UUID requestId,
        String message
) {
}
