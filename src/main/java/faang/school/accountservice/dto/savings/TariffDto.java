package faang.school.accountservice.dto.savings;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record TariffDto(
    Long id,
    String title,
    BigDecimal rate
) {

}
