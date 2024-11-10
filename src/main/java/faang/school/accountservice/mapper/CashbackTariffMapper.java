package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.CashbackTariffDto;
import faang.school.accountservice.entity.cashback.tariff.CashbackTariff;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CashbackTariffMapper {
    CashbackTariffDto toDto(CashbackTariff cashbackTariff);

    CashbackTariff toEntity(CashbackTariffDto cashbackTariffDto);
}
