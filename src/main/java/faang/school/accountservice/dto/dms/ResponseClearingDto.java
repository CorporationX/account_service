package faang.school.accountservice.dto.dms;

import faang.school.accountservice.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseClearingDto {
    private String accountNumber;
    private Long recipientId;
    private BigDecimal balance;
    private Currency currency;
    private String operationId;
    private boolean isForced;
}
