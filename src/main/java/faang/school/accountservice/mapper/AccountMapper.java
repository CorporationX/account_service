package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    AccountDto toAccountDto(Account account);

    Account toAccount(CreateAccountDto accountDto);

    List<AccountDto> toAccountDtos(Stream<Account> accounts);
}
