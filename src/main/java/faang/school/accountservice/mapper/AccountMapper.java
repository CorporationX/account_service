package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.account.AccountCreateProjectDto;
import faang.school.accountservice.dto.account.AccountCreateUserDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    @Mapping(target = "number", source = "accountNumber")
    ResponseAccountDto toResponseAccountDto(Account account);

    @Mapping(target = "type", source = "accountType")
    Account toAccount(AccountCreateUserDto accountCreateUserDto);

    @Mapping(target = "type", source = "accountType")
    Account toAccount(AccountCreateProjectDto accountCreateProjectDto);
}
