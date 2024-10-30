package faang.school.accountservice.mapper;


import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.entity.Tariff;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TariffMapper {

    Tariff toEntity(TariffDto tariffDto);

    TariffDto toDto(Tariff tariff);
}
