package faang.school.accountservice.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TariffDto {
    private Long id;

    @NotEmpty(message = "name cannot be empty")
    private String name;

    @NotNull(message = "rate cannot be null")
    @Positive(message = "rate must be positive")
    private Double rate;
}
