package faang.school.accountservice.dto.cashback;

import faang.school.accountservice.entity.cashback.TransactionType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CashbackRuleCreateDto {
    @Positive
    private Long merchantId;
    private TransactionType transactionType;
    @Min(1)
    @Max(100)
    @NotNull
    private Integer percentage;
}
