package faang.school.accountservice.dto.savings;

import java.math.BigDecimal;

public record TariffDto(
    Long id,
    String title,
    BigDecimal rate
) {

}
