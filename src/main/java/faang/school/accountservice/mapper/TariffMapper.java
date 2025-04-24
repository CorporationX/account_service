package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.TariffResponse;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.entity.tariff.TariffRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TariffMapper {

    @Mapping(target = "activeRate", qualifiedByName = "getActiveTariffRate", source = "rates")
    TariffResponse toDto(Tariff tariff);

    List<TariffResponse> toDtoList(List<Tariff> tariffs);

    @Named("getActiveTariffRate")
    default String getActiveTariffRate(List<TariffRate> rates) {
        if (rates == null || rates.isEmpty()) {
            return null;
        }
        return rates.stream()
                .max(Comparator.comparing(TariffRate::getChangedAt))
                .map(rate -> rate.getRate().toString())
                .orElse(null);
    }
}
