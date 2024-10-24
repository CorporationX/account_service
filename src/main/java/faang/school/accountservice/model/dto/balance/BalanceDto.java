package faang.school.accountservice.model.dto.balance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record BalanceDto(
        long id,

        @Positive(message = "Id must be greater than 0")
        long accountId,
        BigDecimal currentAuthorizationBalance,
        BigDecimal currentActualBalance,

        @JsonFormat(shape = Shape.STRING)
        LocalDateTime createdAt,

        @JsonFormat(shape = Shape.STRING)
        LocalDateTime updatedAt
) {
}
