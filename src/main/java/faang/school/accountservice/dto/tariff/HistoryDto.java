package faang.school.accountservice.dto.tariff;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.accountservice.enums.TariffType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record HistoryDto(
    TariffType name,
    String oldRate,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime activeFrom,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime activeTo
) {
}
