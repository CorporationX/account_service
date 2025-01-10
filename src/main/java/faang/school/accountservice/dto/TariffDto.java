package faang.school.accountservice.dto;

import faang.school.accountservice.enums.TariffType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TariffDto {
    @NotNull
    private Long id;

    @NotNull
    private TariffType type;

    @NotNull
    @Digits(integer = 8, fraction = 2)
    private Double rate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
