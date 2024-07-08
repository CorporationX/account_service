package faang.school.accountservice.dto.payment;

import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDto {
    @NotNull(message = "requester can't be null")
    private BigInteger requesterNumber;

    @NotNull(message = "receiver can't be null")
    private BigInteger receiverNumber;

    @NotNull(message = "currency can't be null")
    private Currency currency;

    @NotNull(message = "amount can't be null")
    private BigDecimal amount;

    @NotNull(message = "idempotency Key can't be null")
    private UUID idempotencyKey;
}