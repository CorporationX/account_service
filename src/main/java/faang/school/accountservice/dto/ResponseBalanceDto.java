package faang.school.accountservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class ResponseBalanceDto {

    private Long id;
    private Long accountId;
    private BigDecimal authorizedBalance;
    private BigDecimal actualBalance;

    private Instant createdAt;
    private Instant updatedAt;
}
