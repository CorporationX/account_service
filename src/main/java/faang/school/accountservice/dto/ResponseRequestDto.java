package faang.school.accountservice.dto;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;

import java.time.LocalDateTime;
import java.util.Map;

public record ResponseRequestDto(
        Long id,
        Long userId,
        RequestType type,
        RequestStatus status,
        Map<String, String> requestInputData,
        String addictionalDetails,
        LocalDateTime createdAt,
        LocalDateTime updateAt
) {
}
