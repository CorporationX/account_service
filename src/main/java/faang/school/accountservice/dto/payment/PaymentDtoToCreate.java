package faang.school.accountservice.dto.payment;

import faang.school.accountservice.model.enums.Currency;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class PaymentDtoToCreate {

    private UUID idempotencyKey;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private Currency currency;
    private BigDecimal amount;
}