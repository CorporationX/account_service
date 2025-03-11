package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.tariff.CreateTariffRequest;
import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.model.tariff.Tariff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TariffMapper {
    @Mapping(target = "rateHistory", expression = "java(List.of(createTariffRequest.rate()))")
    Tariff toEntity(CreateTariffRequest createTariffRequest);

    TariffDto toTariffDto(Tariff tariff);
}