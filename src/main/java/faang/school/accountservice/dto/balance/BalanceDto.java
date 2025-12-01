package faang.school.accountservice.dto.balance;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.accountservice.entity.Account;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record BalanceDto(
        @JsonProperty(value = "id")
        long id,
        @JsonProperty(value = "account_id")
        Account account,
        @JsonProperty(value = "authorization_balance")
        BigDecimal authorizationBalance,
        @JsonProperty(value = "actual_balance")
        BigDecimal actualBalance,
        @JsonProperty(value = "created_at")
        LocalDateTime createdAt,
        @JsonProperty(value = "update_at")
        LocalDateTime updatedAt,
        @JsonProperty(value = "version")
        long version) {
}
