package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.savingsAccount.SavingsAccountResponseDto;
import faang.school.accountservice.entity.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingsAccountMapper {

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "tariff.name", target = "tariffName")
    SavingsAccountResponseDto toDto(SavingsAccount savingsAccount);
}
