package faang.school.accountservice.dto;

import faang.school.accountservice.enums.RequestStatus;

import java.util.UUID;

public record NotificationMessage (
        UUID idpToken,
        Long userId,
        String message,
        RequestStatus status
) {
}
