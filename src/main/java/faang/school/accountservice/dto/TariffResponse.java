package faang.school.accountservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TariffResponse(
        String typeName,
        String activeRate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
