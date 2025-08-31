package faang.school.accountservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceDto {
    private Long id;
    private Long userId;
    private BigDecimal available;
    private BigDecimal reserved;
    private String currency;
}