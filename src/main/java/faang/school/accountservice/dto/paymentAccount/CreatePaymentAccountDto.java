package faang.school.accountservice.dto.paymentAccount;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.PaymentAccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreatePaymentAccountDto(
        Long ownerId,
        Long projectId,
        @NotNull(message = "Currency cannot be null")
        Currency currency,
        @NotNull(message = "Account type cannot be null")
        PaymentAccountType accountType
) {
}
