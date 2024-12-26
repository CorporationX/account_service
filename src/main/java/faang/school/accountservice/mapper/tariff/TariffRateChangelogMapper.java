package faang.school.accountservice.mapper.tariff;

import faang.school.accountservice.dto.tariff.TariffRateDto;
import faang.school.accountservice.entity.tariff.TariffRateChangelog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TariffRateChangelogMapper {

    @Mapping(source = "changeDate", target = "settleDate")
    TariffRateDto toTariffRateDto(TariffRateChangelog rateLog);
}
