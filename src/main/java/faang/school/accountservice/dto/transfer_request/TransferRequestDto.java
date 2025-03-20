package faang.school.accountservice.dto.transfer_request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.PaymentType;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record TransferRequestDto(
        UUID id,
        String senderAccountNumber,
        String receiverAccountNumber,
        BigDecimal amount,
        Currency currency,
        PaymentType paymentType) {
}
