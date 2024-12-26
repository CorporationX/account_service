package faang.school.accountservice.dto.tariff;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TariffRateDto {

    private BigDecimal rate;

    @JsonFormat(pattern = "dd-MM-yy'T'HH:mm:ss")
    private LocalDateTime settleDate;
}
