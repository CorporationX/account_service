package faang.school.accountservice.model.tariff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TariffChangeRecord {
    private Long userId;
    private Instant timestamp;
    private String action;
    private Long oldInterestRateId;
    private Long newInterestRateId;
}