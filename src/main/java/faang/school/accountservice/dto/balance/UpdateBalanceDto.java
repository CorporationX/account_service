package faang.school.accountservice.dto.balance;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record UpdateBalanceDto(
        @JsonProperty(value = "account_id", required = true)
        Long accountId,
        @JsonProperty(value = "authorization_balance")
        BigDecimal authorizationBalance,
        @JsonProperty(value = "actual_balance")
        BigDecimal actualBalance,
        @JsonProperty(value = "current_date_time")
        LocalDateTime currentDateTime) {
}