package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.model.savings.Tariff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TariffMapper {

    @Mapping(target = "currentRate", expression = "java(getLastRate(tariff))")
    TariffDto toTariffDto(Tariff tariff);

    default Double getLastRate(Tariff tariff) {
        return tariff.getRateHistory().isEmpty() ? null :
                tariff.getRateHistory().get(tariff.getRateHistory().size() - 1).getRate();
    }
}