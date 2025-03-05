package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {

    @Mapping(target = "version", ignore = true)
    Account toEntity(AccountDto dto);

    AccountDto toDto(Account account);

    @Mapping(target = "version", ignore = true)
    void updateAccount(@MappingTarget Account account, AccountDto dto);
}
