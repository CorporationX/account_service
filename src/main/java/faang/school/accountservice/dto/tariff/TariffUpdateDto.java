package faang.school.accountservice.dto.tariff;

import faang.school.accountservice.enums.TariffType;

public record TariffUpdateDto(
    TariffType name,
    Double rate

) {
}
