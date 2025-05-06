package faang.school.accountservice.dto.balance;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBalanceDto {
    private static final String INVALID_ID_MSG = "should be more than 1";
    private static final String INVALID_BALANCE_MSG = "should be more than 0";

    @JsonProperty("id")
    @Min(value = 1, message = INVALID_ID_MSG)
    private long id;

    @JsonProperty("authorizationBalance")
    @Min(value = 0, message = INVALID_BALANCE_MSG)
    private Double authorizationBalance;

    @JsonProperty("actualBalance")
    @Min(value = 0, message = INVALID_BALANCE_MSG)
    private Double actualBalance;
}
