package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BalanceDto {
    private UUID id;

    @NotNull(message = "Authorization balance cannot be null")
    private Double authorizationBalance;
    @NotNull(message = "Actual balance cannot be null")
    private Double actualBalance;
}
