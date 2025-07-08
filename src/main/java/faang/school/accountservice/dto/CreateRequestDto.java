package faang.school.accountservice.dto;

import faang.school.accountservice.enums.RequestType;
import lombok.Builder;

import java.util.Map;

@Builder
public record CreateRequestDto(
        Long userId,
        String idempotentToken,
        RequestType type,
        Map<String, String> requestInputData,
        String addictionalDetails
) {
}
