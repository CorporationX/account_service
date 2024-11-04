package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeAccountNumberDto {

    @NotNull(message = "Account type is required and must be a valid type.")
    private AccountType type;

    @Min(value = 1, message = "Value batch size does not meet the minimum requirement.")
    private int batchSize;
}
