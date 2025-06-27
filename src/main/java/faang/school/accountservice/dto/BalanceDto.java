package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDto {
    @NotBlank
    @Pattern(regexp = "\\d{12,20}", message = "Account number must be 12 to 20 digits")
    private String accountNumber;
    private Long accountId;
}