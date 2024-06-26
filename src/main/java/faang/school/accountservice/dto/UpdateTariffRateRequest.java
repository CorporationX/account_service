package faang.school.accountservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateTariffRateRequest {
    private BigDecimal newRate;
}