package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CashbackMerchantMappingDto {

    @Null
    private Long id;

    @Null
    private long cashbackTariffId;

    @NotNull
    @Size(min = 1, max = 50)
    private String merchantName;

    @NotNull
    private BigDecimal percentage;
}
