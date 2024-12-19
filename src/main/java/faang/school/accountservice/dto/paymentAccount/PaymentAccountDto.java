package faang.school.accountservice.dto.paymentAccount;

import com.fasterxml.jackson.annotation.JsonInclude;
import faang.school.accountservice.enums.PaymentAccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.PaymentAccountType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentAccountDto(
        String accountNumber,
        Long ownerId,
        Long projectId,
        Currency currency,
        PaymentAccountType accountType,
        BigDecimal balance,
        PaymentAccountStatus paymentAccountStatus
) {
}
