package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.tariff.TariffResponseDto;
import faang.school.accountservice.entity.Tariff;
import org.springframework.stereotype.Component;

@Component
public class TariffMapper {
    public TariffResponseDto toDto(Tariff tariff) {
        return TariffResponseDto.builder()
            .name(tariff.getName())
            .rate(String.format("%.2f%%", tariff.getRate()))
            .createdAt(tariff.getCreatedAt())
            .updatedAt(tariff.getUpdatedAt())
            .history(tariff.getHistory())
            .build();
    }
}
