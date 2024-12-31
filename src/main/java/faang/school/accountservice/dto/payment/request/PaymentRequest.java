package faang.school.accountservice.dto.payment.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.payment.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private UUID operationId;
    private Long sourceAccountId;
    private Long targetAccountId;
    private BigDecimal amount;
    private Currency currency;
    private Category category;
}
