package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.entity.TariffRateHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TariffMapper {
    @Mapping(source = "rates", target = "currentRate", qualifiedByName = "getCurrentRate")
    TariffDto toDto(Tariff tariff);

    Tariff toEntity(TariffDto tariffDto);

    @Named("getCurrentRate")
    default BigDecimal getCurrentRate(List<TariffRateHistory> rates) {
        if (rates == null || rates.isEmpty()) {
            return null;
        }

        return rates.stream()
                .max(Comparator.comparing(TariffRateHistory::getCreatedAt))
                .map(TariffRateHistory::getRate)
                .orElse(null);
    }
}
