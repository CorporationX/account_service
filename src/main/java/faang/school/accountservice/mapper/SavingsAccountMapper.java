package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.savings_account.CreateSavingsAccountRequest;
import faang.school.accountservice.dto.savings_account.SavingsAccountDto;
import faang.school.accountservice.model.savings_account.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy =  ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface SavingsAccountMapper {
    @Mapping(source = "accountId", target = "account.id")
    SavingsAccount toEntity(CreateSavingsAccountRequest createSavingsAccountRequest);

    @Mapping(source = "savingsAccount.account.id", target = "accountId")
    @Mapping(target = "tariffId", expression = "java(getActualTariffId(savingsAccount))")
    @Mapping(target = "actualRate", source = "actualRate")
    SavingsAccountDto toSavingsAccountDto(SavingsAccount savingsAccount, BigDecimal actualRate);

    default Long getActualTariffId(SavingsAccount savingsAccount) {
        List<Long> tariffHistoryIds = savingsAccount.getTariffHistoryIds();
        return tariffHistoryIds.get(tariffHistoryIds.size()-1);
    }
}