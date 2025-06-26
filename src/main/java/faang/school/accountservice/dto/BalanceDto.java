package faang.school.accountservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDto {
    private Long id;

    @NotBlank
    @Pattern(regexp = "\\d{12,20}", message = "Account number must be 12 to 20 digits")
    private String accountNumber;

    @NotNull
    private BigDecimal authorizationBalance;

    @NotNull
    private BigDecimal actualBalance;

    private Long version;
}
