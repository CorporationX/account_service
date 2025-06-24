package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.UpdateAccountDto;
import faang.school.accountservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "accountType", source = "type")
    AccountDto toDto(Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", source = "accountType")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Account toEntity(AccountDto accountDto);

    void update(UpdateAccountDto accountDto, @MappingTarget Account account);
}
