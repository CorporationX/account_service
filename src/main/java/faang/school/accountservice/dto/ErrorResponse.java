package faang.school.accountservice.dto;

import lombok.Builder;
import java.time.Instant;

import java.util.List;

@Builder
public record ErrorResponse(
        String code,
        Instant timestamp,
        int status,
        String error,
        String message,
        List<String> details,
        String path
) {
}
