package faang.school.accountservice.dto.cashbackdto;

import java.math.BigDecimal;

public record CashbackMappingDto(
        CashbackMappingType mappingType,
        String key,
        BigDecimal percent
) {
}