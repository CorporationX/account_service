package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.savings.SavingsAccountDto;
import faang.school.accountservice.model.savings.Rate;
import faang.school.accountservice.model.savings.SavingsAccount;
import faang.school.accountservice.model.savings.TariffHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Comparator;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingsAccountMapper {

    @Mapping(target = "currentTariff", source = "savingsAccount", qualifiedByName = "mapCurrentTariff")
    @Mapping(target = "currentRate", source = "savingsAccount", qualifiedByName = "mapCurrentRate")
    SavingsAccountDto toSavingsAccountDto(SavingsAccount savingsAccount);

    SavingsAccount toSavingsAccount(SavingsAccountDto savingsAccountdto);

    @Named("mapCurrentTariff")
    default String mapCurrentTariff(SavingsAccount savingsAccount) {
        return savingsAccount.getTariffHistory()
                .stream()
                .max(Comparator.comparing(TariffHistory::getAppliedAt))
                .map(tariffHistory -> tariffHistory.getTariff().getType().name())
                .orElse(null);
    }

    @Named("mapCurrentRate")
    default Double mapCurrentRate(SavingsAccount savingsAccount) {
        return savingsAccount.getTariffHistory()
                .stream()
                .max(Comparator.comparing(TariffHistory::getAppliedAt))
                .flatMap(tariffHistory -> tariffHistory.getTariff().getRateHistory().stream()
                        .max(Comparator.comparing(Rate::getAppliedAt))
                        .map(Rate::getRate))
                .orElse(null);
    }
}

