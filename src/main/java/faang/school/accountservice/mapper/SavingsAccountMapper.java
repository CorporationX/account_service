package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.TariffHistory;
import faang.school.accountservice.entity.TariffRateHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = {AccountMapper.class},
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SavingsAccountMapper {

    @Mapping(source = "tariffs", target = "currentTariff", qualifiedByName = "getCurrentTariff")
    @Mapping(source = "tariffs", target = "currentRate", qualifiedByName = "getCurrentRate")
    SavingsAccountDto toDto(SavingsAccount savingsAccount);

    @Named("getCurrentTariff")
    default String getCurrentTariff(List<TariffHistory> tariffs) {
        if (tariffs == null || tariffs.isEmpty()) {
            return null;
        }

        return tariffs.stream()
                .max(Comparator.comparing(TariffHistory::getCreatedAt))
                .map(history -> history.getTariff().getType())
                .orElse(null);
    }

    @Named("getCurrentRate")
    default BigDecimal getCurrentRate(List<TariffHistory> tariffs) {
        if (tariffs == null || tariffs.isEmpty()) {
            return null;
        }

        return tariffs.stream()
                .max(Comparator.comparing(TariffHistory::getCreatedAt))
                .flatMap(history -> history.getTariff().getRates().stream()
                            .max(Comparator.comparing(TariffRateHistory::getCreatedAt))
                            .map(TariffRateHistory::getRate))
                .orElse(null);
    }
}
