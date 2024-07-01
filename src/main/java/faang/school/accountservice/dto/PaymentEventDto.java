package faang.school.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentEventDto implements Serializable {
    private Long paymentNumber;
    private Long debitAccountId;
    private Long creditAccountId;
    private BigDecimal amount;
    private String type;
    private String currency;
}