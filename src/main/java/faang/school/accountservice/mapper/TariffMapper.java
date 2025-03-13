package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.entity.Tariff;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TariffMapper {
    Tariff toProjectEntity(TariffDto dto);

    TariffDto toTariffResponseDto(Tariff entity);

    List<TariffDto> toTariffResponseDtos(List<Tariff> entities);
}
