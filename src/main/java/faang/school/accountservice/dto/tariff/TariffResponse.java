package faang.school.accountservice.dto.tariff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TariffResponse {

    private long id;
    private String name;
    private BigDecimal currentRate;
}
