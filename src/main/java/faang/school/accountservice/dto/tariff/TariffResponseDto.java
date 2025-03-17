package faang.school.accountservice.dto.tariff;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.accountservice.enums.TariffType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record TariffResponseDto(
    TariffType name,
    String rate,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime updatedAt,

    List<HistoryDto> history
) {
}
