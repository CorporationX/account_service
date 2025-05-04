package faang.school.accountservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ResponseBalanceDto {

    private Long id;
    private Long accountId;
    private BigDecimal authorizedBalance;
    private BigDecimal actualBalance;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
