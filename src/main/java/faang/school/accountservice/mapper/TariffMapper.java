package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.TariffRateHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TariffMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(expression = "java(getCurrentRate(tariff))", target = "currentRate")
    TariffDto toDto(Tariff tariff);

    @Named("getCurrentRate")
    default BigDecimal getCurrentRate(Tariff tariff) {
        List<TariffRateHistory> rateHistory = tariff.getRateHistory();
        return rateHistory.stream()
                .max(Comparator.comparing(TariffRateHistory::getCreatedAt))
                .map(TariffRateHistory::getRate)
                .orElse(BigDecimal.ZERO);
    }
}
