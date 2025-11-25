package faang.school.accountservice.dto;

import faang.school.accountservice.model.RequestType;
import java.util.Map;

public record RequestCreateDto(
        long userId,
        RequestType requestType,
        String lockKey,
        Map<String, Object> inputRequest
) {
}
