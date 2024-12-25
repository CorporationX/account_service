package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.account.AccountDtoOpen;
import faang.school.accountservice.dto.account.AccountDtoResponse;
import faang.school.accountservice.model.account.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface AccountMapper {
    Account toAccount(AccountDtoOpen dto);

    @Mapping(target = "ownerId", source = "owner.id")
    AccountDtoResponse toDto(Account account);
}
