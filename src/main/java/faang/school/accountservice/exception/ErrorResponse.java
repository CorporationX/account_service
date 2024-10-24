package faang.school.accountservice.exception;

import lombok.Builder;

import java.util.Map;

@Builder
public record ErrorResponse(
        String code,
        String message,
        Map<String, String> errors
) {
}
