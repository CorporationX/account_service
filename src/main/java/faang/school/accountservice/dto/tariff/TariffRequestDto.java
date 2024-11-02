package faang.school.accountservice.dto.tariff;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TariffRequestDto {

    @NotNull
    private String tariffName;

    @NotNull
    private Double interestRate;
}
