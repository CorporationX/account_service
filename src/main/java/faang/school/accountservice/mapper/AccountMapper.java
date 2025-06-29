package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountCreationDto;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Account toEntity(AccountDto accountDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Account toCreateEntity(AccountCreationDto accountDto);

    AccountDto toDto(Account account);
}
