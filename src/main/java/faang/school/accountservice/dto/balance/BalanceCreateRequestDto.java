package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BalanceCreateRequestDto {
    @NotBlank(message = "account number must be not blank")
    private String accountNumber;
}
