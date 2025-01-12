package faang.school.accountservice.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AuthorizationMessageEvent {
    @NotBlank
    @NotNull
    private String accountNumber;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    @NotNull
    private String idempotencyToken;
}
