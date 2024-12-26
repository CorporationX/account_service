package faang.school.accountservice.mapper.tariff;

import faang.school.accountservice.dto.tariff.TariffResponse;
import faang.school.accountservice.entity.tariff.Tariff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = TariffRateChangelogMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TariffMapper {

    @Mapping(source = "rateChangelogs", target = "rateHistory")
    TariffResponse toResponse(Tariff tariff);
}
