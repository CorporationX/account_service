package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.TariffAndRateDto;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TariffAndRateMapper {
    @Mapping(source = "tariffHistory", target = "tariffType", qualifiedByName = "toTariffType")
    @Mapping(source = "tariffHistory", target = "rate", qualifiedByName = "toTariffRate")
    TariffAndRateDto mapToDto(SavingsAccount savingsAccount);

    @Named("toTariffType")
    default String toTariffType(List<Tariff> tariffHistory){
        if (tariffHistory == null || tariffHistory.isEmpty()){
            return null;
        }
        return tariffHistory.get(tariffHistory.size() - 1)
                .getType()
                .name();
    }

    @Named("toTariffRate")
    default Double toCurrentTariffRate(List<Tariff> tariffHistory){
        if (tariffHistory == null || tariffHistory.isEmpty()){
            return null;
        }
        var rateHistoryListOfCurrentTariff = tariffHistory.get(tariffHistory.size() - 1).getRateHistoryList();
        return rateHistoryListOfCurrentTariff.isEmpty()
                ? null
                : rateHistoryListOfCurrentTariff.get(rateHistoryListOfCurrentTariff.size() - 1).getRate();
    }
}
