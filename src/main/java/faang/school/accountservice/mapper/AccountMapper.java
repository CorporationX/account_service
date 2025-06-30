package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "accountType", source = "type")
    AccountDto toDto(Account account);

    @Mapping(target = "type", source = "accountType")
    Account toEntity(AccountDto accountDto);
}
