package faang.school.accountservice.mapper.account;

import faang.school.accountservice.dto.account.TariffDto;
import faang.school.accountservice.entity.account.Tariff;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TariffMapper {

    TariffDto toDto(Tariff tariff);
    Tariff toEntity(TariffDto tariffDto);
}
