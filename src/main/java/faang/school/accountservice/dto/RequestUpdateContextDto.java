package faang.school.accountservice.dto;

import java.util.Map;

public record RequestUpdateContextDto(
        Map<String, Object> inputRequest
) {
}
