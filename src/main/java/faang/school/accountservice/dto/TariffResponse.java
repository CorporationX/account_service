package faang.school.accountservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TariffResponse(
        String typeName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
