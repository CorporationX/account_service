package faang.school.accountservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMessageDto {

    @NotNull(message = "Idempotency token обязателен")
    private UUID idempotencyToken;

    @NotNull(message = "fromAccountId обязателен")
    private Long fromAccountId;

    @NotNull(message = "toAccountId обязателен")
    private Long toAccountId;

    @NotNull(message = "amount обязателен")
    @Positive(message = "Сумма должна быть больше нуля")
    private BigDecimal amount;

    @NotBlank(message = "currency обязателен")
    private String currency;

    private LocalDateTime scheduledAt;
}