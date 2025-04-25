package faang.school.accountservice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ErrorResponse(
        String code,
        String message,
        List<String> details
) {
}
