package faang.school.accountservice.dto.balance;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ChangedBalanceDto(
    @JsonProperty(value = "amount")
    @NotNull
    @Positive
    BigDecimal amount){
}
