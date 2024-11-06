package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.entity.CashbackTariff;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CashbackTariffMapper {
    TariffDto toDto(CashbackTariff cashbackTariff);
    CashbackTariff toEntity(TariffDto tariffDto);
}
