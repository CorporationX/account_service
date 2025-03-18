package faang.school.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record RateChangeRequestDto(

        @NonNull
        UUID tariffId,

        @NonNull
        Double newRate,

        @NonNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate effectiveDate
) {
}
