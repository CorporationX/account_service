package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.dto.OwnerDto;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    AccountDto mapToDto(Account account);

    Account mapToEntity(AccountDto accountDto);

    Account createAccountDtoToAccount(CreateAccountDto accountDto);

    OwnerDto mapToOwnerDto(Owner owner);
}
