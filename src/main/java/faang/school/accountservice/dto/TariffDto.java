package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TariffDto(
        Long id,
        @NotBlank
        String type,
        @NotNull
        BigDecimal currentRate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
