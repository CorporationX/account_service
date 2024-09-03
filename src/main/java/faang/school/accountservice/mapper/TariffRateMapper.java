package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.TariffAndRateDto;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Stack;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TariffRateMapper {
    @Mapping(source = "tariffHistory", target = "tariffType", qualifiedByName = "toTariffType")
    @Mapping(source = "tariffHistory", target = "rate", qualifiedByName = "toTariffRate")
    TariffAndRateDto mapToDto(SavingsAccount savingsAccount);

    @Named("toTariffType")
    default String toTariffType(Stack<Tariff> tariffHistory){
        if (tariffHistory == null || tariffHistory.empty()){
            return null;
        }
        return tariffHistory.peek()
                .getType()
                .name();
    }

    @Named("toTariffRate")
    default Double toTariffRate(Stack<Tariff> tariffHistory){
        if (tariffHistory == null || tariffHistory.empty()){
            return null;
        }
        return tariffHistory.peek()
                .getRateHistoryList()
                .isEmpty() ? null : tariffHistory.peek().getRateHistoryList().peek().getRate();
    }
}
