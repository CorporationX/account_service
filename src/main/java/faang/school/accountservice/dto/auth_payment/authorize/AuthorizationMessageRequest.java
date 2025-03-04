package faang.school.accountservice.dto.auth_payment.authorize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import faang.school.accountservice.enums.Currency;

import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthorizationMessageRequest(
        UUID id,
        Long senderAccountId,
        Long receiverAccountId,
        BigDecimal amount,
        Currency currency,
        String paymentType) {
}
