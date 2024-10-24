package faang.school.accountservice.mapper;


import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.model.account.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    @Mapping(target = "owner.externalId", source = "externalId")
    @Mapping(target = "owner.type", source = "ownerType")
    @Mapping(target = "type", source = "accountType")
    @Mapping(target = "status", source = "accountStatus")
    Account toAccountEntity(AccountDto accountDto);

    @Mapping(target = "externalId", source = "owner.externalId")
    @Mapping(target = "ownerType", source = "owner.type")
    @Mapping(target = "accountType", source = "type")
    @Mapping(target = "accountStatus", source = "status")
    AccountDto toAccountDto(Account account);

    List<AccountDto> toAccountDtoList(List<Account> accounts);
}
