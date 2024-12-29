package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record BalanceCreateDto(
    @NotNull
    long accountId,
    @NotNull
    BigDecimal authorizedValue
) {


}
