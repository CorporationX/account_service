package faang.school.accountservice.dto.request;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;

import java.time.LocalDateTime;
import java.util.Map;

public record ResponseRequestDto(
        Long id,
        String idempotencyToken,
        Long paymentAccountId,
        RequestType requestType,
        Map<String, Object> input,
        RequestStatus requestStatus,
        String statusNote,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
