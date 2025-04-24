package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TariffUpdateRequest(

        @NotNull(message = "Id cannot be null")
        Long id,

        String typeName,
        BigDecimal rate
) {
}
