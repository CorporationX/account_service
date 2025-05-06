package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.SavingsAccountResponse;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.tariff.TariffHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SavingsAccountMapper {

    @Mapping(target = "activeTariff", qualifiedByName = "getActiveTariff", source = "tariffHistory")
    @Mapping(target = "activeTariffRate", ignore = true)
    SavingsAccountResponse toDto(SavingsAccount savingsAccount);

    @Named("getActiveTariff")
    default String getActiveTariff(List<TariffHistory> tariffHistory) {
        if (tariffHistory == null || tariffHistory.isEmpty()) {
            return null;
        }
        return tariffHistory.stream()
                .max(Comparator.comparing(TariffHistory::getAppliedAt))
                .map(history -> history.getTariff().getTypeName())
                .orElse(null);
    }
}
