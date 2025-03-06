package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.savings_account.CreateSavingsAccountRequest;
import faang.school.accountservice.dto.savings_account.SavingsAccountDto;
import faang.school.accountservice.model.savings_account.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy =  ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface SavingsAccountMapper {
    @Mapping(source = "accountId", target = "account.id")
    SavingsAccount toEntity(CreateSavingsAccountRequest createSavingsAccountRequest);

    @Mapping(source = "savingsAccount.account.id", target = "accountId")
    SavingsAccountDto toSavingsAccountDto(SavingsAccount savingsAccount, BigDecimal actualRate);
}