package faang.school.accountservice.dto.tariff;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TariffResponseDto {
    private Long id;
    private String name;
    private List<BigDecimal> rateHistory;
    private String createdAt;
    private String updatedAt;
}
