package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BalanceDto {
    private Long id;

    @NotNull(message = "Account id cannot be null")
    private Long accountId;
    @NotNull(message = "Authorization balance cannot be null")
    private Double authorizationBalance;
    @NotNull(message = "Actual balance cannot be null")
    private Double actualBalance;
    @NotNull(message = "Version cannot be null")
    private Long version;
}
