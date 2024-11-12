package faang.school.accountservice.mapper.tariff;

import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.mapper.rate.RateMapper;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = RateMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TariffMapper {

    @Mapping(source = "rate", target = "rateDto")
    TariffDto toDto(Tariff tariff);

    List<TariffDto> toDtos(List<Tariff> tariffs);
}
