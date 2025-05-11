package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.entity.Tariff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface TariffMapper {

    @Mapping(target = "savingsAccountRates", ignore = true)
    @Mapping(target = "tariffHistories", ignore = true)
    Tariff toEntity(TariffDto tariffDto);

    @Mapping(target = "rate", ignore = true)
    TariffDto toDto(Tariff tariff);
}
