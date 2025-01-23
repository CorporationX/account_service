package faang.school.accountservice.mapper.tariff;

import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.entity.tariff.Tariff;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TariffMapper {
    TariffDto toDto(Tariff entity);
    List<TariffDto> toDto(List<Tariff> entity);
}
