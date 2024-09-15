package faang.school.accountservice.mapper.account;

import faang.school.accountservice.dto.account.SavingsAccountDto;
import faang.school.accountservice.entity.account.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingsAccountMapper {
    @Mapping(source = "account.id", target = "accountId")
    SavingsAccountDto toDto(SavingsAccount savingsAccount);
    @Mapping(target = "account", ignore = true)
    SavingsAccount toEntity(SavingsAccountDto savingsAccountDto);
}
