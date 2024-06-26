package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.tariff.CreateTariffDto;
import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.model.Rate;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.TariffHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TariffMapper {

    @Mapping(target = "rateHistory", source = "rateHistory", ignore = true)
    TariffDto toDto(Tariff tariff);

    Tariff toEntity(CreateTariffDto tariffDto);
}
