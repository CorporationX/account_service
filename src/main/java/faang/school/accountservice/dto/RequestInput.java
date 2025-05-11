package faang.school.accountservice.dto;

import faang.school.accountservice.enums.Currency;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RequestInput {

    private String sourceAccount;
    private String targetAccount;
    private BigDecimal amount;
    private Currency currency;
}
