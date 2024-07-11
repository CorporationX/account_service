package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.model.Tariff;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TariffMapper {
    TariffDto toDto(Tariff tariff);

    Tariff toEntity(TariffDto tariffDto);
}
