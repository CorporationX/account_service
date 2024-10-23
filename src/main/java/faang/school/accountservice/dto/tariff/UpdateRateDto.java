package faang.school.accountservice.dto.tariff;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRateDto {
    @NotNull
    private Long tariffId;

    @NotNull
    @Positive
    private Double newRate;
}
