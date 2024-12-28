package faang.school.accountservice.dto;

import java.time.LocalDateTime;

public record HistoryDto(
        String previousValue,
        String newValue,
        LocalDateTime at
) {
}
