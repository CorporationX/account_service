package faang.school.accountservice.dto.transaction;

import faang.school.accountservice.enums.Currency;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class TransactionDtoToCreate {

    private UUID idempotencyKey;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private Currency currency;
    private BigDecimal amount;
}