package faang.school.accountservice.mappers;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.model.Tariff;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TariffMapper {
    TariffDto toDto(Tariff tariff);
    Tariff toEntity(TariffDto tariffDto);
}
