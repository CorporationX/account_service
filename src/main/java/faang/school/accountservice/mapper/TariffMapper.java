package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.model.RateHistory;
import faang.school.accountservice.model.Tariff;
import org.mapstruct.*;


import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TariffMapper {
    @Mapping(target = "rateHistory", source = "rateHistory", qualifiedByName = "rateHistoryToDoubles")
    TariffDto toDto(Tariff tariff);

    @Mapping(target = "rateHistory", source = "rateHistory", qualifiedByName = "doublesToRateHistory")
    Tariff toEntity(TariffDto tariffDto);

    @Named("rateHistoryToDoubles")
    default List<Double> rateHistoryToDoubles(List<RateHistory> rateHistories) {
        if (rateHistories == null) {
            return null;
        }
        return rateHistories.stream()
                .map(RateHistory::getRate)
                .collect(Collectors.toList());
    }

    @Named("doublesToRateHistory")
    default List<RateHistory> doublesToRateHistory(List<Double> rates) {
        if (rates == null) {
            return null;
        }
        return rates.stream()
                .map(rate -> {
                    RateHistory rateHistory = new RateHistory();
                    rateHistory.setRate(rate);
                    return rateHistory;
                })
                .collect(Collectors.toList());
    }
}
