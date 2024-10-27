package faang.school.accountservice.mapper;


import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.entity.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = AccountMapper.class)
public interface SavingsAccountMapper {

    @Mapping(target = "account", source = "savingsAccountDto")
    SavingsAccount toEntity(SavingsAccountDto savingsAccountDto);
}
