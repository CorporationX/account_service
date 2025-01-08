package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.account.SavingsAccountDto;
import faang.school.accountservice.model.account.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface SavingsAccountMapper {

    @Mapping(source = "account.id", target = "accountId")
    SavingsAccountDto toSavingsAccountDto(SavingsAccount savingsAccount);

    @Mapping(source = "account.id", target = "accountId")
    List<SavingsAccountDto> toDtos(List<SavingsAccount> savingsAccounts);
}