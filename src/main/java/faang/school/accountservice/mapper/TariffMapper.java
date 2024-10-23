package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.tariff.TariffCreateDto;
import faang.school.accountservice.dto.tariff.TariffResponseDto;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.TariffRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TariffMapper {
    Tariff toEntity(TariffCreateDto dto);

    @Mapping(target = "rates", source = "tariffRates", qualifiedByName = "mapTariffRatesToRates")
    TariffResponseDto toResponseDto(Tariff entity);

    List<TariffResponseDto> toResponseDtos(List<Tariff> entities);

    @Named("mapTariffRatesToRates")
    default List<Double> mapTariffRatesToRates(List<TariffRate> tariffRates) {
        if (tariffRates == null) {
            return new ArrayList<>();
        }

        return tariffRates.stream()
                .sorted(Comparator.comparing(TariffRate::getCreatedAt))
                .map(TariffRate::getRate)
                .toList();
    }
}
