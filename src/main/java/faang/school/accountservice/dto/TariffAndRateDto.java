package faang.school.accountservice.dto;

import lombok.Builder;

@Builder
public record TariffAndRateDto(
        String tariffType,
        Double rate
) {}
