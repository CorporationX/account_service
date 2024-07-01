package faang.school.accountservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateTariffRequest {
    private String name;
    private List<BigDecimal> rateHistory;
}
