package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.model.SavingsAccountTariffHistory;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.TariffRateHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SavingAccountMapper {
    @Mapping(source = "account.id", target = "id")
    @Mapping(source = "account.number", target = "accountNumber")
    @Mapping(expression = "java(getCurrentTariffInfo(savingsAccount))", target = "currentTariffId")
    @Mapping(expression = "java(getCurrentTariffRate(savingsAccount))", target = "currentTariffRate")
    @Mapping(source = "lastInterestCalculatedDate", target = "lastInterestCalculatedDate")
    @Mapping(source = "account.balance.currentBalance", target = "currentBalance")
    @Mapping(source = "account.balance.authorizedBalance", target = "authorizedBalance")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    SavingsAccountDto toDto(SavingsAccount savingsAccount);

    @Named("getCurrentTariffInfo")
    default Long getCurrentTariffInfo(SavingsAccount savingsAccount) {
        SavingsAccountTariffHistory currentTariffHistory = getCurrentTariffHistory(savingsAccount);
        return currentTariffHistory.getTariff().getId();
    }

    @Named("getCurrentTariffRate")
    default BigDecimal getCurrentTariffRate(SavingsAccount savingsAccount) {
        SavingsAccountTariffHistory currentTariffHistory = getCurrentTariffHistory(savingsAccount);
        Tariff currentTariff = currentTariffHistory.getTariff();
        return getCurrentRate(currentTariff);
    }

    default SavingsAccountTariffHistory getCurrentTariffHistory(SavingsAccount savingsAccount) {
        List<SavingsAccountTariffHistory> tariffHistory = savingsAccount.getTariffHistory();
        return tariffHistory.stream()
                .filter(history -> history.getEndDate() == null)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Current tariff history not found"));
    }

    default BigDecimal getCurrentRate(Tariff tariff) {
        List<TariffRateHistory> rateHistory = tariff.getRateHistory();
        TariffRateHistory currentRate = rateHistory.stream()
                .max(Comparator.comparing(TariffRateHistory::getCreatedAt))
                .orElseThrow(() -> new ResourceNotFoundException("Current rate not found"));
        return currentRate.getRate();
    }
}
