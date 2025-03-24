package faang.school.accountservice.dto.cashback;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.accountservice.entity.cashback.MerchantType;
import faang.school.accountservice.entity.cashback.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public class CashbackRuleReadDto {
    private Long id;
    private Integer percentage;
    @JsonProperty("transaction_type")
    private TransactionType transactionType;
    @JsonProperty("merchant_id")
    private Long merchantId;
    @JsonProperty("merchant_type")
    private MerchantType merchantType;
}
