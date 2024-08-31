package faang.school.accountservice.dto;

import faang.school.accountservice.enums.TariffType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Evgenii Malkov
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TariffDto {
    @NotNull
    private TariffType type;
    @NotNull
    @Min(0)
    @DecimalMax("99.99")
    private BigDecimal percent;
}
