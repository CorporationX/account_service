package faang.school.accountservice.dto.savings_account;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingsAccountRequestDto {
    @NotNull(message = "accountId is required")
    private Long accountId;
    @NotNull(message = "tariffId is required")
    private Long tariffId;
}