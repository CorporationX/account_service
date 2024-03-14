package faang.school.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceDto {

    private Long id;
    private BigDecimal authorizationBalance;
    private BigDecimal actualBalance;
    private Instant updatedAt;
    private Long version;

}
