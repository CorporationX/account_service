package faang.school.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDto {
    private Long id;

    private BigDecimal authorizationBalance;

    private BigDecimal actualBalance;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private Long accountId;

    private int version;
}
