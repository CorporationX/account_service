package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountRate;
import faang.school.accountservice.enums.TariffType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TariffDto {

    private List<Double> bettingHistory;

    @NotNull
    @Positive
    private Double currentRate;

    @NotNull
    private TariffType type;
}
