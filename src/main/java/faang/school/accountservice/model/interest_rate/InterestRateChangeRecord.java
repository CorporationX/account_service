package faang.school.accountservice.model.interest_rate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterestRateChangeRecord {
    private Long userId;
    private Instant timestamp;
    private String action;
    private BigDecimal oldValue;
    private BigDecimal newValue;
}