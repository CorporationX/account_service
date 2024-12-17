package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.account.AccountDtoOpen;
import faang.school.accountservice.dto.account.AccountDtoResponse;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.owner.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface AccountMapper {
    @Mapping(target = "owner", source = "owner")
    Account toAccount(AccountDtoOpen dto, Owner owner);

    @Mapping(target = "ownerId", source = "owner.ownerId")
    @Mapping(target = "ownerType", source = "owner.type")
    AccountDtoResponse toDto(Account account);
}
